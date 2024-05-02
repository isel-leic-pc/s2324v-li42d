package pt.isel.pc.jht.lecture20.completable_futures.combine_1

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

import java.lang.Thread.*

fun <T,U,V> CompletableFuture<T>.depoisCombina(
    other: CompletionStage<U>,
    fn: (T, U) -> V
) : CompletableFuture<V> {
    val fut = CompletableFuture<V>()

    var r1 : T? = null
    var r2 : U? = null

    val isSecond = AtomicBoolean(false)

    fun tryToComplete(err: Throwable?) {
        if (err != null) {
            fut.completeExceptionally(err)
        } else {
            if (isSecond.compareAndExchange(false, true)) {
                val res1 = r1
                val res2 = r2
                if (res1 != null && res2 != null) {
                    fut.complete(fn(res1, res2))
                } else {
                    fut.completeExceptionally(NullPointerException())
                }
            }
        }
    }

    this.whenComplete { res, err ->
        r1 = res
        tryToComplete(err)
    }

    other.whenComplete { res, err ->
        r2 = res
        tryToComplete(err)
    }

    return fut
}

fun asyncFunction1() : CompletableFuture<String> =
    CompletableFuture.supplyAsync<String> {
        println("++ [FUN1] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(3000)
        println("++ [FUN1] END (T${ currentThread().threadId() }) ++")
        "ISEL"
        //throw Exception("async1 failed")
    }

fun asyncFunction2(base: Int) : CompletableFuture<Int> =
    CompletableFuture.supplyAsync<Int> {
        println("++ [FUN2] BEGIN (T${ currentThread().threadId() }) ++")
        sleep(2000)
        println("++ [FUN2] END (T${ currentThread().threadId() }) ++")
        base + 24
        //throw Exception("async2 failed")
    }

fun main() {
    println(":: [MAIN] STARTING (T${ currentThread().threadId() }) ::")
    sleep(2000)

    val asyncRes1 = asyncFunction1()

    val asyncRes2 = asyncFunction2(2000)

    val asyncRes12 = asyncRes1.depoisCombina(asyncRes2) {
        txt, num -> "$txt - $num"
    }

    val asyncRes = asyncRes12.handle { res, err ->
        println("~~ [RES] RESULTS (T${ currentThread().threadId() }) ~~")
        if (err == null) {
            println("result: $res")
        } else {
            println("ERROR: $err")
        }
    }

    println(":: [MAIN] ALL READY (T${ currentThread().threadId() }) ::")

    asyncRes.join()

    println(":: [MAIN] ALL DONE (T${ currentThread().threadId() }) ::")
}
