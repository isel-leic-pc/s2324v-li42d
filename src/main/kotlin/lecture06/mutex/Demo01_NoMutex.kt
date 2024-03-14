package pt.isel.pc.jht.lecture06.mutex

import kotlin.concurrent.thread

class UnsynchronizedCounter {
    private var value = 0

    fun increment() {
        value += 1
    }

    fun read() : Int {
        return value
    }
}

// Aren't unit tests better that test programs?
fun main() {
    val NTHREADS = 8
    val NITERATIONS = 1000000

    val obj = UnsynchronizedCounter()

    (0 until NTHREADS).map {
        thread {
            repeat(NITERATIONS) {
                obj.increment()
            }
        }
    }.forEach(Thread::join)

    println("Final value: ${ obj.read() } (expected: ${ NTHREADS * NITERATIONS })")
}
