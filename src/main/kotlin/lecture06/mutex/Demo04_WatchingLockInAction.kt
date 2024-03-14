package pt.isel.pc.jht.lecture06.mutex

import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class LockWatching {
    private val locker = ReentrantLock()
    private var value = 0

    fun accumulate(delta: Int, doze: Duration) {
        println("Waiting for my turn to add $delta")
        locker.withLock {
            println("Adding $delta then sleeping ${doze.seconds} seconds")
            value += delta
            sleep(doze)  // Never do this! Hold locks for short times only.
        }
    }
}

const val NTHREADS = 8

fun main() {
    val obj = LockWatching()

    val threads = List(NTHREADS) {
        thread { obj.accumulate(it, Duration.ofSeconds((NTHREADS - it).toLong())) }
    }

    threads.forEach(Thread::join)
}