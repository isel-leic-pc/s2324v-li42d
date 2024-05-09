package pt.isel.pc.jht.lecture29.coroutines.delay

import kotlin.coroutines.*
import kotlinx.coroutines.*

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val executor = Executors.newSingleThreadScheduledExecutor()

fun altDelayTerminate() { executor.shutdown() }

suspend fun altDelay(time: Long) {
	suspendCoroutine<Unit> { continuation : Continuation<Unit> ->
		executor.schedule({ continuation.resume(Unit) }, time, TimeUnit.MILLISECONDS)
	}
	//println("... waking up ...")
}