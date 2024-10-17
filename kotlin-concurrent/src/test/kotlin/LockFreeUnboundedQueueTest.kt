package concurrent

import kotlin.test.Test

class LockFreeUnboundedQueueTest {

  @Test
  fun addFirst() {
    val queue = LockFreeUnboundedQueue<Int>()
    queue.addLast(1)
    queue.addLast(2)
    queue.addLast(3)
    queue.forEach { println(it) }
  }
}