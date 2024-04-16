
function delayedValue(value, delay) {
	return new Promise((resolve, reject) => {
		// after 'delay' milliseconds, fulfill the promise with 'value'
		setTimeout(() => { resolve(value) }, delay)
	})
}

const promise = delayedValue(1234, 3000)

console.log(":: promise created ::")

promise.then(value => {
	// will run only after the promise is fulfilled
	console.log(`>> value produced: ${ value } <<`)
})

console.log(":: callback registered ::")

console.log(":: waiting... ::")
