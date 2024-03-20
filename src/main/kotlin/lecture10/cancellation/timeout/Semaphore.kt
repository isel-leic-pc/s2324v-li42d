package pt.isel.pc.jht.synchronizers.kernel

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import java.util.LinkedList
import kotlin.concurrent.withLock
import kotlin.time.Duration

// Using kernel-style.
//
// Threads are attended by order of arrival.
// This avoids starvation, but any thread will have
// to wait for its turn, even if enough permits are
// available for its request.
//
// This version supports cancellation of blocked
// acquires via interrupts or timeout.

class FairSemaphore(private var permits: Int) {
	private val locker = ReentrantLock()

	private inner class Request(
		val units: Int,
		val condition: Condition = locker.newCondition(),
		var done: Boolean = false
	)

	private val requests = LinkedList<Request>()

	@Throws(InterruptedException::class)
	fun acquire(units: Int = 1, timeout: Duration) : Boolean {
		locker.withLock {
			// fast-path
			if (requests.isEmpty() && units <= permits) {
				permits -= units
				return true
			}

			// wait-path
			val myRequest = Request(units)
			requests.addLast(myRequest)
			
			var remainingTime = timeout.inWholeNanoseconds
			try {
				while (true) {
					remainingTime = myRequest.condition.awaitNanos(remainingTime)
					if (myRequest.done) {  // success must have priority over timeout
						return true
					}
					if (remainingTime <= 0) {  // timeout
						quitWaiting(myRequest)
						return false
					}
				}
			} catch (ie: InterruptedException) {
				if (myRequest.done) {  // interrupted and released at the same time
					Thread.currentThread().interrupt()   // delay interrupt effects
					return true
				}
				quitWaiting(myRequest)
				throw ie
			}
		}
	}
	
	fun release(units: Int = 1) {
		locker.withLock {
			permits += units
			releaseWithPermits()
		}
	}
	
	private fun releaseWithPermits() {
		while (true) {
			val firstRequest = requests.peekFirst()
			if (firstRequest == null) {
				return  // Request list is empty
			}
			if (firstRequest.units <= permits) {
				permits -= firstRequest.units
				firstRequest.done = true
				firstRequest.condition.signal()
				requests.removeFirst()
			} else {
				return  // Not enough permits for the oldest request
			}
		}
	}
	
	private fun quitWaiting(myRequest: Request) {
		// Available permits could be insufficient for the first
		// blocked thread, but maybe they are enough to release
		// one or more of the following blocked threads.
		if (requests.peekFirst() == myRequest) {
			requests.removeFirst()
			releaseWithPermits()
		} else {
			requests.remove(myRequest)
		} 
	} 
}










