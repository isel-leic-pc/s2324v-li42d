package pt.isel.pc.jht.lecture06.mutex

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.concurrent.thread

class Demo01Test {

    private val NTHREADS = 8
    private val NITERATIONS = 1000000

    /**
     * Unit test to demonstrate concurrent incrementing of an unprotected variable.
     *
     * Note: While it's highly unlikely, in some rare cases the final result may
     * indeed match the expected value, due to the non-deterministic nature of
     * thread scheduling and execution. This test is designed to showcase potential
     * issues related to concurrent operations rather than provide a guaranteed
     * result. It's essential to understand that the outcome can vary, even if that
     * is extremely unlikely.
     */
    @Test
    fun multithreadedIncrements() {
        val obj = UnsynchronizedCounter()

        (0 until NTHREADS).map {
            thread {
                repeat(NITERATIONS) {
                    obj.increment()
                }
            }
        }.forEach(Thread::join)

        // (see Note in the above documentation comment)
        assertNotEquals(NTHREADS * NITERATIONS, obj.read())
    }
}