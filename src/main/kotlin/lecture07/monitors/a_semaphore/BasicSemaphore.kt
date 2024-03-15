package pt.isel.pc.jht.monitors

import java.util.concurrent.locks.*
import kotlin.concurrent.*

class BasicSemaphore(private var permits : Int) {
	private val locker = ReentrantLock()
	private val waitSet = locker.newCondition()

	fun acquire() {
		locker.withLock {
			// Replace this 'while' with an 'if' and run the program a few times.
			// You will likely see the test failing. Waiting in a monitor must be
			// guarded by a 'while', due to the possibility of barging.
			while (permits <= 0) {
				// (lock acquired)
				waitSet.await() // (lock not acquired)
				// (lock acquired)
			}
			--permits
		}
	}
	
	fun release() {
		locker.withLock {
			++permits
			waitSet.signal()
		}
	}
} 

class MaxChecker(val max : Int) {
	private var count = 0
	
	var maxSeen = 0
		private set;
		
	@Synchronized
	fun inc() {
		if (++count > maxSeen) {
			maxSeen = count
		}
	}
	
	@Synchronized
	fun dec() {
		--count
	}
	
	@Synchronized
	fun hasFailed() = maxSeen > max
}

val sem = BasicSemaphore(1)
val checker = MaxChecker(1)

val NTHREADS = 32

fun main() {
	val curTime = System.currentTimeMillis()
	val endTime = curTime + 10000

	(0 until NTHREADS).map {
		thread {
			while (System.currentTimeMillis() < endTime) {
				sem.acquire()
				checker.inc()
				// ...
				checker.dec()
				sem.release()
			}
		}
	}.forEach(Thread::join)
	
	println("Result: " +
		if (checker.hasFailed()) "FAIL(${checker.maxSeen})" else "PASS" )
}
