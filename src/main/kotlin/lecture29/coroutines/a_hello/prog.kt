package pt.isel.pc.jht.lecture29.coroutines.hello

import kotlinx.coroutines.*

fun main() = runBlocking {

	launch {
		delay(5000)
		println(", world!")
	}

	print("Hello")
}
