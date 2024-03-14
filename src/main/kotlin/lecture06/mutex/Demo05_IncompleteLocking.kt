package pt.isel.pc.jht.lecture06.mutex

import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SemiSynchronizedList {
    private val locker = ReentrantLock()
    private val list = LinkedList<Long>()

    fun add(n: Long) {
        val n1 = n*2+1
        val n2 = n*2+3
        locker.withLock() {
            list.add(n1)

            list.add(n2)
        }
    }

    // SACRILEGE! Never expose private mutable objects, even if disguised as immutable!
    //            NOTE THAT LOCKING IS NOT ENOUGH TO SAVE THIS FUNCTION!
    fun items() : List<Long> {
        locker.withLock {
            return list
        }
    }

    // SACRILEGE! Never access data guarded by a lock without taking the lock!
    //            EVEN IF IT'S "JUST" FOR READING! EVEN IF IT'S "JUST" FOR READING!
    //            EVEN IF IT'S "JUST" FOR READING! EVEN IF IT'S "JUST" FOR READING!
    //            EVEN IF IT'S "JUST" FOR READING! EVEN IF IT'S "JUST" FOR READING!
    //            (keep repeating...)
    fun sum() : Long {
        //locker.withLock {
            return list.sum()
        //}
    }
}
