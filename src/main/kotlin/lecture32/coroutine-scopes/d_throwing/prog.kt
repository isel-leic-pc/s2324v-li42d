package pt.isel.pc.jht.lecture32.coroutines.cancel.throwing

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
        launch {
            println("[$tid] :: OPERATION 1 ::")
            val r1 = operation(10)
            println("[$tid] r1: ${ r1 }")
        }

        // The call to operation(20) in the following coroutine will throw
        // an exception.  It will cancel the coroutine, and will be
        // propagated to its parent, which will be cancelled too.

        launch {
            println("[$tid] :: OPERATION 2 ::")
            val r2 = operation(20)
            println("[$tid] r2: ${ r2 }")
        }

        // The cancellation of the second coroutine will be propagated to
        // its parent.  Cancelling the parent coroutine will cancel all
        // its children coroutines.  We may observe that by catching the
        // cancellation exception in this third coroutine.

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
    }

    println("## THE END ##")
}
