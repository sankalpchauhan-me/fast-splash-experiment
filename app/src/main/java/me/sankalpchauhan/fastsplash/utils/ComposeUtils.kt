package me.sankalpchauhan.fastsplash.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import kotlin.math.max
import kotlin.math.min

/**
 * Signals when the composable is visible and when not
 */
@Stable
@Composable
fun Modifier.onVisibilityChanged(
    threshold: Float = 0.3f, // 30% visible
    onVisible: (percentVisible: Float) -> Unit,
    onInvisible: (() -> Unit)? = null
): Modifier {
    val rootView = LocalView.current
    return this.onGloballyPositioned { coords ->
        val self: Rect = coords.boundsInWindow()

        // Root view bounds in window coordinates
        val xy = IntArray(2)
        rootView.getLocationInWindow(xy)
        val viewport = Rect(
            xy[0].toFloat(),
            xy[1].toFloat(),
            xy[0] + rootView.width.toFloat(),
            xy[1] + rootView.height.toFloat()
        )

        // Intersection area
        val interLeft   = max(self.left, viewport.left)
        val interTop    = max(self.top, viewport.top)
        val interRight  = min(self.right, viewport.right)
        val interBottom = min(self.bottom, viewport.bottom)

        val interW = (interRight - interLeft).coerceAtLeast(0f)
        val interH = (interBottom - interTop).coerceAtLeast(0f)
        val interArea = interW * interH
        val selfArea = self.width * self.height
        val percent = if (selfArea > 0f) interArea / selfArea else 0f

        if (percent >= threshold) onVisible(percent) else onInvisible?.invoke()
    }
}