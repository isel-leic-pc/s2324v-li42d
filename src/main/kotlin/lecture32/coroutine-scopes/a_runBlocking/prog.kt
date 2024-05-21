package pt.isel.pc.jht.lecture32.coroutines.scopes.runBlocking

import kotlinx.coroutines.*

val tid : Long
    inline get() = Thread.currentThread().threadId()

suspend fun work(crname: String, n: Int) {
    repeat(n) {
        println("[T$tid,$crname] working...")
        delay(2000)
    }
    println("[T$tid,$crname] -- ending --")
}

fun main() {
    println("[T$tid,main] -- STARTING --")

    // runBlocking is a regular (non-suspend) function, allowing
    // transitions from non-suspending to suspending code.
    // Introduces a CoroutineContext and creates a new CoroutineScope.

    //runBlocking(Dispatchers.Default) {   // With Default dispatcher, coroutines will run on pooled threads
    runBlocking {         // By default, runBlocking coroutines run on the same thread

        println("[T$tid,CR0] :: launching... ::")

        launch {
            work("CR1", 3)
        }

        launch {
            work("CR2", 5)
        }

        launch {
            work("CR3", 4)
        }

        println("[T$tid,CR0] :: all launched ::")
    }

    // The runBlocking block will wait for all the coroutines to terminate
    // before exiting.  The following line will run last.

    println("[T$tid,main] -- ENDING --")
}
