package pt.isel.pc.jht.lecture32.coroutines.cancel.jobCancel

import kotlinx.coroutines.*

val tid : Long
    inline get() = Thread.currentThread().threadId()

suspend fun operationStep(value: Int, name: String): Int {
    println("[$tid] operation($value) : $name")
    if (value == 22) {
        throw Exception("operation step failure (value: $value)")
    }
    delay(1000)
    return value + 1
}

suspend fun operation(initialValue: Int): Int {
    var value = initialValue

    value = operationStep(value, "step 1")
    value = operationStep(value, "step 2")
    value = operationStep(value, "step 3")
    value = operationStep(value, "step 4")

    println("[$tid] operation($value) : return")
    return value
}

suspend fun main() {
    coroutineScope {
        val job1 = launch {
            try {
                coroutineScope {
                    launch {
                        println("[$tid] :: OPERATION 11 ::")
                        val r11 = operation(110)
                        println("[$tid] r11: ${ r11 }")
                    }
                    delay(500)
                    launch {
                        println("[$tid] :: OPERATION 12 ::")
                        val r12 = operation(120)
                        println("[$tid] r12: ${ r12 }")
                    }
                }
            } catch (exc: Exception) {
                println("[$tid] :: FAILURE IN 1 ::")
                println("[$tid] exc: ${ exc.message }")
                exc.printStackTrace()
                throw exc
            }
        }

        launch {
            println("[$tid] :: OPERATION 2 ::")
            val r2 = operation(200)
            println("[$tid] r2: ${ r2 }")
        }

        launch {
            println("[$tid] :: OPERATION 3 ::")
            try {
                val r3 = operation(30)
                println("[$tid] r3: ${ r3 }")
            } catch (exc: Exception) {
                println("[$tid] :: FAILURE IN 3 ::")
                println("[$tid] exc: ${ exc.message }")
                exc.printStackTrace()
                throw exc
            }
        }

        // An explicit request to cancel the first child coroutine
        // terminates its execution.  However it does not cause the
        // cancellation of its parent.  All other coroutines will
        // continue running.

        delay(1200)
        job1.cancel()
    }

    println("## THE END ##")
}
