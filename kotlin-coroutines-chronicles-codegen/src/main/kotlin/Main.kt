package coroutines.chronicles.codegen

import kotlin.coroutines.*

/* Note:
 Throughout the document term "coroutine" will represent either a suspend lambda or a suspend function,
 which is different from the usual definition of coroutines - something like a lightweight thread.
 The document reuses the term since "suspend lambda or function" is wordy,
 and when it requires the typical definition, it says explicitly "a coroutine in a broad sense."
 */

// ####################
// ## Suspend Lambda ##
// ####################

//suspend fun dummy() {}
//
//suspend fun main() {
//  val lambda: suspend () -> Unit = {
//    dummy()
//    println(1)
//    dummy()
//    println(2)
//  }
//  lambda()
//}

// Which, upon running, will print 1 and 2, as expected.

// One can call a suspend function only from other suspend function or suspend lambda,
// but it can call ordinary, non-suspendable functions.

var c: Continuation<Unit>? = null

suspend fun suspendMe(): Unit = suspendCoroutine {
  println("Suspended")
  c = it
}
//
//suspend fun main() {
//  val lambda: suspend () -> Unit = {
//    suspendMe()
//    println(1)
//    suspendMe()
//    println(2)
//  }
//  lambda()
//}

// Now, when we run the code, it prints Suspended and nothing else;
// since lambda is, in fact, suspended, and it suspends suspend fun main as well.
// Well note here, we also do not resume anywhere, but the above argument still stands.
// That practically we cannot call anywhere resume() as the callee (main) is also suspended.

// To fix the issue with the suspension of main,
//  we need to cross a boundary between suspendable and ordinary worlds and make main ordinary,
//  so, when it starts a coroutine, and the coroutine suspends, main does not.

// Since one cannot call a suspendable function from an ordinary one,
// there are special functions, so-called coroutine builders,
// whose sole purpose is to create a coroutine, run it, and when it suspends, return execution to the caller.
// Other than that, they act like other ordinary functions.
fun builder(lambda: suspend () -> Unit) {
  lambda.startCoroutine(object : Continuation<Unit> {
    override val context: CoroutineContext get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
      result.getOrThrow()
    }
  })
}
//
//fun main() {
//  val lambda: suspend () -> Unit = {
//    suspendMe()
//    println(1)
//    suspendMe()
//    println(2)
//  }
//  builder { lambda() }
//  // The call from the builder returns immediately.
//  // Without resuming the continuation, the program will exit.
//  c!!.resume(Unit)
//  c!!.resume(Unit)
//  // Here we will get an error:
//  // IllegalStateException: Already resumed
//  c!!.resume(Unit)
//}

// ###################
// ## State Machine ##
// ###################


// The section about state-machines touched upon the COROUTINE_SUSPENDED marker
// and said that suspending functions and lambdas return the marker when they suspend.
// Consequently, every suspend function return returnType | COROUTINE_SUSPENDED union type.

// ################################
// ## Continuation Passing Style ##
// ################################

fun main() {
  val a: suspend () -> Unit = { suspendMe() }
  val b: suspend () -> Unit = { a() }
  builder { b() }
  c?.resume(Unit)
}

// suspendMe here, as in the previous example, suspends. Stack trace inside suspendMe look like (skipping non-relevant parts)

/*
 suspendMe
main$a$1.invokeSuspend
main$a$1.invoke
main$b$1.invokeSuspend
main$b$1.invoke
// ...
builder
main
*/

// Now a continues its execution and returns Unit.
// But the execution returns to BaseContinuationImpl.resumeWith.
// However, we need to continue the execution of b since b called a.
// In other words, we need to store a link to b somewhere in a,
// so then, inside BaseContinuationImpl.resumeWith, we can call b's resumeWith,
// which then resumes the execution of b. Remember, b is a coroutine,
// and all coroutines inherit BaseContinuationImpl, which has the method resumeWith.
// Thus, we need to pass b to a.

// Since we add a continuation parameter to each suspend function and lambda,
// we cannot call suspending functions or lambdas from ordinary functions,
// and we cannot call them by passing null as the parameter since the coroutine call resumeWith on it.
// Instead, we should use coroutine builders, which provide root continuation and start the coroutine.
// That is the reason for the two worlds model.

