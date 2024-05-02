package pt.isel.pc.jht.lecture20.completable_futures.compose

import java.util.concurrent.CompletableFuture

import java.lang.Thread.*

fun asyncFunction1(base: Int) : CompletableFuture<Int> =
    CompletableFuture.supplyAsync<Int> {
        println("++ [FUN1] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(2000)
        println("++ [FUN1] END (T${ currentThread().threadId() }) ++")
        base + 24
    }

fun asyncFunction2(num: Int) : CompletableFuture<String> =
    CompletableFuture.supplyAsync<String> {
        println("++ [FUN2] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(3000)
        println("++ [FUN2] END (T${ currentThread().threadId() }) ++")
        "ISEL - $num"
    }

fun printResults(res: String) {
    println("~~ [RES] RESULTS [T${ currentThread().threadId() }] ~~")
    println("result: $res")
}

fun main() {
    println(":: [MAIN] STARTING (T${ currentThread().threadId() }) ::")
    sleep(2000)

    val asyncRes1 = asyncFunction1(2000)

    val asyncRes12 = asyncRes1.thenCompose(::asyncFunction2)

    val asyncRes = asyncRes12.thenAccept(::printResults)

    println(":: [MAIN] ALL READY (T${ currentThread().threadId() }) ::")

    asyncRes.join()

    println(":: [MAIN] ALL DONE (T${ currentThread().threadId() }) ::")
}
