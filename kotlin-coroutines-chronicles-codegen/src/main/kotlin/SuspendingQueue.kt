package coroutines.chronicles.codegen

import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SuspendingList<T>(capacity: Int) {
  private val waiters = ConcurrentLinkedDeque<ContinuationSlot<T, Unit>>()
  private val _list = ConcurrentLinkedDeque<T>()
  private val maxCapacity = capacity
  private val size get() = _list.size

  // Only suspending operation will be "add()".
  // Remove operation will try to resume waiters.

  fun tryAdd(value: T): Boolean {
    if (size >= maxCapacity) return false
    _list.add(value)
    return true
  }

  fun tryRemove(): T? {
    if (size < 1) return null
    _list.removeLast()
    // Try to resume the first waiter.
    val slot = waiters.poll()
    if (tryAdd(slot.value)) {
      slot.continuation.resume(Unit)
    } else {
      waiters.addFirst(slot)
    }
    return slot.value
  }

  suspend fun add(value: T) {
    if (tryAdd(value)) return // fast-path
    addSlowPath(value)
  }

  private suspend fun addSlowPath(value: T) = suspendCoroutine { cont ->
    // Try to suspend.
    val slot = ContinuationSlot(value, cont)
    waiters.add(slot)
  }
}

private class ContinuationSlot<T, R>(val value: T, val continuation: Continuation<R>)

// Core problem here that we have not something like join() implemented.
fun main() {
  val list = SuspendingList<Int>(5)
  builder {
    repeat(10) {
      list.add(it + 1)
      println("Added:${it + 1}")
    }
    repeat(10) {
      println("removed:${list.tryRemove()}")
    }
  }

}
