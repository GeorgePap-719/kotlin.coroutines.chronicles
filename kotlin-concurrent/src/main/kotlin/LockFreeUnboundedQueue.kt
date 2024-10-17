package concurrent

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class LockFreeUnboundedQueue<T> {
  internal val head: AtomicRef<Node<T>>
  internal val tail: AtomicRef<Node<T>>

  init {
    val node = Head<T>()
    head = atomic(node)
    tail = atomic(node)
  }

  private val isHead = tail.value is Head<T>

  // 1. update next node
  // 2. update node with cur pos
  // 3. update prev ptr
  fun addLast(value: T) {
    val next = Node(value)
    while (true) {
      val cur = tail.value
      cur.addLast(next)
      if (tail.compareAndSet(cur, next)) return
    }
  }

  fun forEach(action: (T) -> Unit) {
    var cur = head.value.next.value
    while (true) {
      val value = cur?.value ?: return
      action(value)
      cur = cur.next.value ?: return
    }
  }
}

fun <T> Node(value: T): Node<T> = Node<T>().apply { this.cur.value = value }


open class Node<T> {
  internal val cur = atomic<T?>(null)
  internal val next = atomic<Node<T>?>(null)
  internal val prev = atomic<Node<T>?>(null)

  val value get() = cur.value!!

  fun setNext(value: Node<T>): Boolean = next.compareAndSet(null, value)

  fun addLast(value: Node<T>) {
    var cur = this
    while (true) {
      if (!cur.setNext(value)) {
        cur = next.value!!.next.value!! // always succeeds
        continue
      }
      val cas = value.prev.compareAndSet(null, cur)
      check(cas) { "At this point, no else should be able to modify this" }
      return
    }
  }
}

/* Sentinel nodes */
class Head<T> : Node<T>()

