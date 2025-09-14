#!/usr/bin/env bash
set -euo pipefail


PKG="me.sankalpchauhan.fastsplash"
ACTIVITY="me.sankalpchauhan.fastsplash/.presentation.listing.MainActivity"


TAG="PERF"
SENTINEL="TRACE_STOPPED"

ITERATIONS=35
COLD_START=true
LAUNCH_WAIT=true
TIMEOUT_SECS=10
SLEEP_BETWEEN=2
OUT="perf_runs.csv"

# Optional: pin to a specific device: export ANDROID_SERIAL=... or set ADB below
ADB="${ADB:-adb}"

#####################################
# Helpers
#####################################

# Use line-buffering if stdbuf exists (helps grep/tee flush promptly)
if command -v stdbuf >/dev/null 2>&1; then
  STDBUF="stdbuf -oL"
else
  STDBUF=""
fi

# Extract the last integer on the first line matching a label
# Robust to tabs/spaces: finds first line containing label, prints last numeric token
extract_metric() {
  local file="$1"; local label="$2"
  # shellcheck disable=SC2002
  grep -m1 -F "$label" "$file" 2>/dev/null \
    | awk '{
        for (i=NF; i>0; i--) {
          if ($i ~ /^[0-9]+$/) { print $i; exit }
        }
      }'
}

# Parse am start -W output for ThisTime/TotalTime/WaitTime (ms)
parse_am_times() {
  local am="$1"
  local total this wait
  total=$(printf "%s\n" "$am" | sed -n 's/^TotalTime:[[:space:]]*\([0-9]\+\).*/\1/p' | head -n1)
  this=$( printf "%s\n" "$am" | sed -n 's/^ThisTime:[[:space:]]*\([0-9]\+\).*/\1/p' | head -n1)
  wait=$( printf "%s\n" "$am" | sed -n 's/^WaitTime:[[:space:]]*\([0-9]\+\).*/\1/p' | head -n1)
  echo "${total:-},${this:-},${wait:-}"
}

# CSV header
if [ ! -f "$OUT" ]; then
  echo "iter,device_epoch_ms,am_total_ms,am_this_ms,am_wait_ms,page_ready_ms,fcp_ms,fpt_ms,stop_line" > "$OUT"
fi

#####################################
# Main loop
#####################################
for ((i=1; i<=ITERATIONS; i++)); do
  echo "=== Iteration $i/$ITERATIONS ==="

  # Clean state
  $ADB shell am force-stop "$PKG" >/dev/null 2>&1 || true
  if [ "$COLD_START" = true ]; then
    $ADB shell pm clear "$PKG" >/dev/null 2>&1 || true
  fi

  # Prepare log capture
  TMP="$(mktemp -t perf_iter_${i}.XXXXXX.log)"
  trap 'rm -f "$TMP"' EXIT

  $ADB logcat -c

  # Start a background pipeline:
  #   logcat (filtered by TAG) -> tee TMP -> grep until first SENTINEL (then exit)
  (
    # -v epoch gives stable timestamps; level filter set to :V to allow D lines too
    # Filter by your TAG, squelch others with "*:S"
    # shellcheck disable=SC2086
    $ADB logcat -v epoch -s "${TAG}":V "*:S" \
    | $STDBUF tee "$TMP" \
    | $STDBUF grep -m1 -F "$SENTINEL" >/dev/null
  ) &
  watcher=$!

  # Launch app and optionally parse ActivityManager times
  AM_TIMES=",,"
  if [ "$LAUNCH_WAIT" = true ]; then
    AM_OUT="$($ADB shell am start -W -n "$ACTIVITY" 2>/dev/null | tr -d '\r')"
    AM_TIMES="$(parse_am_times "$AM_OUT")"
  else
    $ADB shell am start -n "$ACTIVITY" >/dev/null 2>&1
  fi

  # Wait for watcher or timeout (portable without `timeout(1)`)
  steps=$((TIMEOUT_SECS * 10))  # 0.1s steps
  timed_out=1
  for ((t=0; t<steps; t++)); do
    if ! kill -0 "$watcher" 2>/dev/null; then
      timed_out=0; break
    fi
    sleep 0.1
  done

  # Kill the background subshell if still running
  if kill -0 "$watcher" 2>/dev/null; then
    kill "$watcher" 2>/dev/null || true
  fi

  # Read stop line (or timeout marker)
  if [ "$timed_out" -eq 0 ]; then
    STOP_LINE="$(grep -m1 -F "$SENTINEL" "$TMP" | tr ',' ';')"
  else
    STOP_LINE="TIMEOUT_${TIMEOUT_SECS}s_no_${SENTINEL}"
  fi

  # Parse your three metrics from TMP
  PAGE_READY="$(extract_metric "$TMP" 'Page Ready')"
  FCP="$(extract_metric "$TMP" 'First Content Painted Time')"
  FPT="$(extract_metric "$TMP" 'Fully Painted Time')"

  # Device-side timestamp (helps align with log)
  DEV_EPOCH_MS="$($ADB shell date +%s%3N 2>/dev/null | tr -d '\r')"

  # Append CSV
  echo "$i,$DEV_EPOCH_MS,$AM_TIMES,${PAGE_READY:-},${FCP:-},${FPT:-},${STOP_LINE:-}" >> "$OUT"

  # Cleanup, small pause to reduce thermal drift
  rm -f "$TMP"
  sleep "$SLEEP_BETWEEN"
done

echo "Done. Results in: $OUT"