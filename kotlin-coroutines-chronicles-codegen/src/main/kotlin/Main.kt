package coroutines.chronicles.codegen

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SuspendGun {
  private var c: Continuation<Int>? = null


  suspend fun gun(): Int {
    val c = suspendCoroutine<Int> {
      c = it
    }
    return c
  }

  fun resumeGun() {
    c!!.resume(10)
  }

}

suspend fun main() {
  val susGun = SuspendGun()

  println(susGun.gun())

  susGun.resumeGun()
}