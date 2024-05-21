package pt.isel.pc.jht.lecture34.suspend_synch.semaphore

import java.util.LinkedList

import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SuspendableSemaphore(initialPermits: Int) {
    private val mutex = Mutex()
    private var currentPermits = initialPermits

    private class Request() {
        var continuation: Continuation<Unit>? = null
        var done = false
    }
    private val waiters = LinkedList<Request>()

    suspend fun acquire() {
        // fast-path
        mutex.lock()
        if (currentPermits >= 1) {
            currentPermits -= 1
            mutex.unlock()
            return
        }

        // suspend-path
        val request = Request()
        try {
            suspendCancellableCoroutine { cont ->
                request.continuation = cont
                waiters.add(request)
                mutex.unlock()
            }
        } catch (cancelExc: CancellationException) {
            handleCancellation(cancelExc, request)
        }
    }

    suspend fun release() {
        mutex.withLock {
            if (waiters.size == 0) {
                currentPermits += 1
                null
            } else {
                val request = waiters.removeFirst()
                request.done = true
                request.continuation
            }
        }?.resume(Unit)  // Don't run continuations while holding the Mutex
    }

    private suspend fun handleCancellation(cause: Throwable?, request: Request) {
        mutex.withLock {
            if (!request.done) {
                waiters.remove(request)
                throw cause ?: Exception("-- unexpected call to handleCancellation --")
            }
            // else succeed
            // NOTE: the coroutine is still cancelled; the caller
            //       is responsible for dealing with that situation
            //       (acquire returned with permits but the
            //        coroutine was cancelled simultaneously)
        }
    }
}
