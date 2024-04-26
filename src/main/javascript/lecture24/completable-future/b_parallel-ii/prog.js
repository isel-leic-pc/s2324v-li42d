
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

const asyncRes = Promise.all([asyncRes1, asyncRes2])

asyncRes.then((res) => {
	console.log("~~ [RES] RESULTS ~~")
	console.log(`result: ${ res[0] } - ${ res[1] }`)
}).then(() => {
	console.log(":: [DONE] ALL DONE ::")
})

console.log(":: [MAIN] ALL READY ::")
