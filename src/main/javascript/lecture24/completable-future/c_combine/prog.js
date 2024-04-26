
Promise.prototype.thenCombine = function (that, combiner) {
	return Promise.all([this, that])
	              .then(res => combiner(res[0], res[1]))
}

function asyncFunction1() {
	return new Promise((resolve) => {
		console.log("++ [FUN1] BEGIN ++")
		setTimeout(() => {
			console.log("++ [FUN1] END ++")
			resolve("ISEL")
		}, 3000)
	})
}

function asyncFunction2(base) {
	return new Promise((resolve) => {
		console.log("++ [FUN2] BEGIN ++")
		setTimeout(() => {
			console.log("++ [FUN2] END ++")
			resolve(base + 24)
		}, 2000)
	})
}

console.log(":: [MAIN] STARTING ::")

const asyncRes1 = asyncFunction1()

const asyncRes2 = asyncFunction2(2000)

const asyncRes12 = asyncRes1.thenCombine(asyncRes2,
	(txt, num) => `${txt} - ${num}`
)

const asyncRes = asyncRes12.then((res) => {
	console.log("~~ [RES] RESULTS ~~")
	console.log(`result: ${ res }`)
}).then(() => {
	console.log(":: [DONE] ALL DONE ::")
})

console.log(":: [MAIN] ALL READY ::")
