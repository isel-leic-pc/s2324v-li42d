package pt.isel.pc.jht.lecture06.mutex

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class CounterWithExplicitLock {
    private val locker = ReentrantLock()
    private var value = 0

    fun increment() {
        locker.lock()
        try {
            value += 1
        } finally {
            locker.unlock()
        }
    }

    fun read() : Int {
        locker.lock()   // for visibility (we'll discuss this later)
        try {
            return value
        } finally {
            locker.unlock()
        }
    }
}

// Aren't unit tests better that test programs?
fun main() {
    val NTHREADS = 8
    val NITERATIONS = 1000000

    val obj = CounterWithIntrinsicLock()

    (0 until NTHREADS).map {
        thread {
            repeat(NITERATIONS) {
                obj.increment()
            }
        }
    }.forEach(Thread::join)

    println("Final value: ${ obj.read() } (expected: ${ NTHREADS * NITERATIONS })")
}
