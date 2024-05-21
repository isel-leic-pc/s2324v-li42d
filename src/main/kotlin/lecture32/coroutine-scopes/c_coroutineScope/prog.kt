package pt.isel.pc.jht.lecture32.coroutines.scopes.coroutineScope

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

// main may be declared as a suspend function, and it will run in a
// CoroutineContext, but it will be invoked without a CoroutineScope.
// You may create one immediately and avoid using GlobalScope.

suspend fun main() = coroutineScope {

    println("[T$tid,main/CR0] -- STARTING --")

    println("[T$tid,main/CR0] :: before first launch... ::")

    launch {
        work("CR1", 3)
    }

    launch {
        work("CR2", 5)
    }

    println("[T$tid,main/CR0] :: after first launch... ::")

    // A new CoroutineScope may be introduced with coroutineScope,
    // which reuses the current CoroutineContext.

    coroutineScope {

        println("[T$tid,main/CR0] >> in a new scope <<")

        launch {
            work("CR3", 4)
        }

        launch {
            work("CR4", 3)
        }

        println("[T$tid,main/CR0] >> end of the new scope <<")
    }
    println("[T$tid,main/CR0] >> OUT of the new scope <<")

    println("[T$tid,main/CR0] :: before second launch... ::")

    // In this case, new coroutines may be launched directly.

    launch {
        work("CR5", 3)
    }

    launch {
        work("CR6", 5)
    }

    println("[main/CR0] :: after second launch... ::")

    println("[main/CR0] -- ENDING --")

    // All coroutines will run to their end as the
    // coroutine scope will wait before exiting
}
