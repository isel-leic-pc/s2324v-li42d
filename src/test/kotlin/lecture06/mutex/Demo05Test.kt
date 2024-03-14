package pt.isel.pc.jht.lecture06.mutex

import org.junit.jupiter.api.Test
import java.lang.Thread.*
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

import kotlin.concurrent.thread

class Demo05Test {

    private val NTHREADS = 8
    private val NITERATIONS = 100000

    fun testAddingWithLockAndCheckingWithout(check: (SemiSynchronizedList) -> Boolean) {
        val list = SemiSynchronizedList()
        var observedAnOddSum = false
        var checkerThreadEndedOk = false
        val checkCounter = AtomicInteger()

        val adderThreads = List(NTHREADS) {
            thread {
                repeat(NITERATIONS) {
                    list.add(it.toLong())
                }
            }
        }

        val checkerThread = thread {
            try {
                repeat(NTHREADS * NITERATIONS) {
                    if (!check(list)) {
                        observedAnOddSum = true
                    }
                    checkCounter.incrementAndGet()
                    Thread.sleep(Duration.ofNanos(20_000))
                }
                println("Checker done")
                checkerThreadEndedOk = true
            } catch (iex: InterruptedException) {
                checkerThreadEndedOk = true
            }
        }

        adderThreads.forEach(Thread::join)
        println("Adder threads done")
        checkerThread.interrupt()
        checkerThread.join()

        println("Total checks: ${ checkCounter.get() }")
        assert(!observedAnOddSum) { "An odd sum has been observed" }
        assert(checkerThreadEndedOk) { "The checker thread terminated with an exception" }
    }

    @Test
    fun items() {
        testAddingWithLockAndCheckingWithout { list ->
            list.items().sum() % 2 == 0L
        }
    }

    @Test
    fun sum() {
        testAddingWithLockAndCheckingWithout { list ->
            list.sum() % 2 == 0L
        }
    }
}