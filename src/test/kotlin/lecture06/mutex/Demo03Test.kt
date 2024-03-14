package pt.isel.pc.jht.lecture06.mutex

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.concurrent.thread

class Demo03Test {

    private val NTHREADS = 8
    private val NITERATIONS = 1000000

    /**
     * Unit test to demonstrate concurrent incrementing of a variable using
     * an explicit ReentrantLock for synchronization.
     */
    @Test
    fun multithreadedIncrements() {
        val obj = CounterWithExplicitLock()

        (0 until NTHREADS).map {
            thread {
                repeat(NITERATIONS) {
                    obj.increment()
                }
            }
        }.forEach(Thread::join)

        assertEquals(NTHREADS * NITERATIONS, obj.read())
    }
}