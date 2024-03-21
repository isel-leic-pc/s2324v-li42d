package pt.isel.pc.jht.vthreads.demo03

import java.time.Duration

//val builder = Thread.ofPlatform()
val builder = Thread.ofVirtual()

const val NTHREADS = 32
const val DEMO_SECONDS = 10L

fun main() {
    val counters = Array(NTHREADS) { 0L }

    println(":: Starting demo threads ::")
    val endDemoTime = System.currentTimeMillis() + Duration.ofSeconds(DEMO_SECONDS).toMillis()
    val threads = (0 until NTHREADS).map { n ->
        builder.start {
            var counter = 0L
            while (System.currentTimeMillis() < endDemoTime) {
                counter++
            }
            counters[n] = counter
        }
    }

    println(":: Waiting for demo threads to finish ::")
    threads.forEach(Thread::join)

    println(":: RESULTS ::")
    counters.forEachIndexed { index, count ->
        println("[%02d] $count".format(index))
    }
}