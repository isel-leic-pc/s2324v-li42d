package pt.isel.pc.jht.lecture31.suspend_synch.latch

import kotlin.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import kotlin.collections.*

class SuspendableLatch() {
    companion object {
        private val NO_WAITERS = emptyList<Continuation<Unit>>()
    }

    private val mutex = Mutex()   // Don't use a ReentrantLock to synchronize coroutines.
    private var isOpen = false

    private val waiters = mutableListOf<Continuation<Unit>>()

    suspend fun await() {
        // fast path
        mutex.lock()
        if (isOpen) {
            mutex.unlock()
            return
        }

        // suspend path
        println(">> suspending <<")
        suspendCoroutine<Unit> { cont ->
            waiters.add(cont)
            mutex.unlock()
        }
        println(">> proceeding <<")
    }

    suspend fun open() {
        mutex.withLock {
            if (!isOpen) {
                isOpen = true
                waiters.toList().also { waiters.clear() }
            } else {
                NO_WAITERS
            }
        }.forEach { waiter ->
            waiter.resume(Unit)   // Do not run arbitrary code while holding the mutex.
        }
    }
}

