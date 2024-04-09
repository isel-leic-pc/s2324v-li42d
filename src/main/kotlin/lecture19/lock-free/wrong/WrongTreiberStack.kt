package pt.isel.pc.jht.lecture19.lock_free.wrong

import java.util.concurrent.atomic.AtomicReference
import java.util.Collections
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/* Using an intrusive list exposes the list nodes and
 * allows them to be reused in a push after a pop.
 * This makes this code vulnerable to the ABA problem.
 */

public open class Node<T>(var next: T?)

class WrongTreiberStack<T : Node<T>> {

	private val head: AtomicReference<T?> = AtomicReference(null)

	fun push(node: T) {
		while (true) {
			val oldHeadNode = head.get()
			val newHeadNode = node.apply { next = oldHeadNode }
			if (head.compareAndSet(oldHeadNode, newHeadNode)) {
				return
			}
		}
	}

	fun pop(): T? {
		while (true) {
			val oldHeadNode = head.get()
			if (oldHeadNode == null) {
				return null
			}
			val newHeadNode = oldHeadNode.next
			if (head.compareAndSet(oldHeadNode, newHeadNode)) {
				return oldHeadNode
			}
		}
	}
}

class IntNode(val num : Int) : Node<IntNode>(null)

fun main() {
	val NTHREADS = 100
	val TDURATION = 10

	val endTime = System.currentTimeMillis() + TDURATION * 1000

	val stack = WrongTreiberStack<IntNode>()
	val nodes = Collections.synchronizedSet(mutableSetOf<IntNode>())

	(1..NTHREADS).map { tn ->
		var node : IntNode? = IntNode(tn)
		thread {
			while (System.currentTimeMillis() < endTime) {
				pause()
				stack.push(node.nullChecked(":: [T$tn] node is null ::"))
				pause()
				node = stack.pop()
			}
			nodes.add(node)
		}
	}.forEach { t -> t.join() }

	println(
		if (nodes.count() == NTHREADS) "SUCCESS" else "FAILURE"
	)
}

private fun pause() {
  /* Uncommenting the following line increases the probability 
   * of success for the test program. With the line commented
   * out, the test program is much more likely to fail. */	 
   //TimeUnit.MILLISECONDS.sleep(1L)
}

private inline fun <reified T> T?.nullChecked(message: String) : T {
	if (this == null) {
		println(message)
		throw NullPointerException()
	}
	return this
}
