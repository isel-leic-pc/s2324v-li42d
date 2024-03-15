package pt.isel.pc.jht.synchronizers

import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import java.util.concurrent.*
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

// ###############################################
// #                                             #
// # WARNING: Naive code. Do not use as example! #
// #                                             #
// ###############################################

// The author tries to keep track of blocked and signaled threads
// using counters. This is very hard to keep accurate and therefore
// extremely error-prone.
// Not only that, but it is not even good enough to distinguish
// different groups of threads.
// In the test code, a first group of threads waits in the gate.
// Immediately after opening the gate, a second group of thread
// arrive and the gate is closed. Some threads from the first
// group are unable to pass, but a few from the second group are
// released in their place.

class NaiveGate(private var open : Boolean = false) {
	private val lock = ReentrantLock()
	private val waitSet = lock.newCondition()

	private var waiting = 0      //  =:-O  (scary!)
	private var authorized = 0   //  =:-O  (scary!)
	
	val isOpen
		get() = lock.withLock { open }
	
	fun open() {
		lock.withLock {
			if (!open) {
				open = true;
				authorized += waiting
				waitSet.signalAll()
			}
		}
	}

	fun close() {
		lock.withLock {
			if (open) {
				open = false;
			}
		}
	}
	
	fun await() {
		lock.withLock {
			while (!open && authorized == 0) {
				++waiting
				waitSet.await()
				--waiting
			}
			if (authorized > 0) {
				--authorized
			}
		}
	}
}

val gate = NaiveGate()

val done1 = AtomicInteger(0)
val done2 = AtomicInteger(0)
val latch = CountDownLatch(1)

fun action1() {
	gate.await()
	done1.incrementAndGet()
}

fun action2() {
	latch.await()
	gate.await()
	done2.incrementAndGet()
}

fun main() {
	repeat(100) { thread(isDaemon = true) { action1() } }
	repeat(100) { thread(isDaemon = true) { action2() } }
	
	TimeUnit.SECONDS.sleep(1)     // WARNING: do not use sleep for synchronization!

	// Change the sleeping time to see how many threads finish from each group.

	gate.open()
	latch.countDown()
	TimeUnit.MICROSECONDS.sleep(100)     // WARNING: do not use sleep for synchronization!
	gate.close()
	
	TimeUnit.SECONDS.sleep(5)     // WARNING: do not use sleep for synchronization!
	
	println("terminated threads 1: ${ done1.get() }")
	println("terminated threads 2: ${ done2.get() }")
}