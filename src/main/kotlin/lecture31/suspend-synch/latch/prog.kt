package pt.isel.pc.jht.lecture31.suspend_synch.latch

import kotlin.coroutines.*
import kotlinx.coroutines.*

val tid : Long
    inline get() = Thread.currentThread().threadId()

fun main() {
    runBlocking {
        val latch = SuspendableLatch()

        launch {
            println("++ [T$tid] Waiting 1 ++")
            latch.await()
            println("++ [T$tid] DONE #1 ++")
        }

        launch {
            println("++ [T$tid] Waiting 2 ++")
            latch.await()
            println("++ [T$tid] DONE #2 ++")
        }

        launch {
            println("++ [T$tid] Waiting 3 ++")
            latch.await()
            println("++ [T$tid] DONE #3 ++")
        }

        println(":: [T$tid] STARTING ::")
        delay(10000)
        println(":: [T$tid] RELEASING ::")

        latch.open()
    }
}
