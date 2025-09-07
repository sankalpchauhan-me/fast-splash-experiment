package me.sankalpchauhan.perftracker

open class PerfTrace(open val name: String) {
    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var isStarted: Boolean = false
    var isStopped: Boolean = false

    constructor(name: String, endTimestamp: Long): this(name){
        endTime = endTimestamp
        isStarted=true
        isStopped = true
    }

    open fun startTrace(){
        if(!isStarted){
            startTime = System.currentTimeMillis()
            isStarted = true
        }
    }

    open fun stopTrace(endTimestamp: Long = System.currentTimeMillis()){
        if(!isStopped){
            endTime = endTimestamp
            isStopped = true
        }
    }

    open fun getDuration(): Long{
        return endTime-startTime
    }

    fun getElapsedTime(): Long{
        return System.currentTimeMillis() - startTime
    }
}