package pt.isel.pc.jht.lecture20.completable_futures.combine

import java.util.concurrent.CompletableFuture

import java.lang.Thread.*

fun asyncFunction1() : CompletableFuture<String> =
    CompletableFuture.supplyAsync<String> {
        println("++ [FUN1] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(3000)
        println("++ [FUN1] END (T${ currentThread().threadId() }) ++")
        "ISEL"
    }

fun asyncFunction2(base: Int) : CompletableFuture<Int> =
    CompletableFuture.supplyAsync<Int> {
        println("++ [FUN2] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(2000)
        println("++ [FUN2] END (T${ currentThread().threadId() }) ++")
        base + 24
    }

fun main() {
    println(":: [MAIN] STARTING (T${ currentThread().threadId() }) ::")

    val asyncRes1 = asyncFunction1()

    val asyncRes2 = asyncFunction2(2000)

    val asyncRes12 = asyncRes1.thenCombine(asyncRes2) {
        txt, num -> "$txt - $num"
    }

    val asyncRes = asyncRes12.thenAccept { res ->
        println("~~ [RES] RESULTS (T${ currentThread().threadId() }) ~~")
        println("result: ${ res }")
    }

    /*
        asyncFunction1()
            .thenCombine(
                asyncFunction2(2000),
                { txt, num -> "$txt - $num" }
            )
            .thenAccept { res ->
                // ...
            }
    */

    println(":: [MAIN] ALL READY (T${ currentThread().threadId() }) ::")

    asyncRes.join()

    println(":: [MAIN] ALL DONE (T${ currentThread().threadId() }) ::")
}
