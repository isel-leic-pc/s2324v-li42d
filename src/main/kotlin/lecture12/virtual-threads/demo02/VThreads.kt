package pt.isel.pc.jht.vthreads.demo02

import java.util.concurrent.CountDownLatch

//val builder = Thread.ofPlatform()
val builder = Thread.ofVirtual()

val latch = CountDownLatch(1)

val task = { latch.await() }

fun main() {
    println(":: Creating infinite threads ::")
    var nthreads = 0
    try {
        while (true) {
            builder.start(task)
            nthreads++
            if (nthreads % 10000 == 0)
                println("$nthreads threads created")
        }
    } catch (ex: Exception) {
        println(":: Failed after creating $nthreads threads ::")
        throw ex
    }
}