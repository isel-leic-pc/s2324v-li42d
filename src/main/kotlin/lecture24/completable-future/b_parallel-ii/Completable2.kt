package pt.isel.pc.jht.lecture20.completable_futures.parallel_2

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

    val asyncRes12 = CompletableFuture.allOf(asyncRes1, asyncRes2)

    val asyncRes = asyncRes12.thenRun {
        println("~~ [RES] RESULTS (T${ currentThread().threadId() }) ~~")
        println("result: ${ asyncRes1.get() } - ${ asyncRes2.get() }")
    }

    println(":: [MAIN] ALL READY (T${ currentThread().threadId() }) ::")

    asyncRes.join()

    println(":: [MAIN] ALL DONE (T${ currentThread().threadId() }) ::")
}
