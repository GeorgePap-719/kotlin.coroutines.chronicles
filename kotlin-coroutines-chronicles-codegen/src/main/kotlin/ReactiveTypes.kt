//@file:OptIn(ExperimentalTypeInference::class)

package coroutines.chronicles.codegen


fun interface Publisher<T> {
  fun subscribe(subscriber: Subscriber<T>)
}

class PublisherImpl<T>(private val block: Subscriber<T>.() -> Unit) : Publisher<T> {
  override fun subscribe(subscriber: Subscriber<T>) {
    subscriber.block()
  }
}

fun <T> publish(/*@BuilderInference*/ block: Subscriber<T>.() -> Unit): Publisher<T> = PublisherImpl(block)

fun <T, R> Publisher<T>.transform(
  /*@BuilderInference*/ transform: Subscriber<R>.(value: T) -> Unit
): Publisher<R> = publish {
  subscribe { value ->
    transform(value)
  }
}

fun <T, R> Publisher<T>.map(transform: (T) -> R): Publisher<R> = transform { value ->
  return@transform onNext(transform(value))
}

fun <T> Publisher<T>.filter(predicate: (T) -> Boolean): Publisher<T> = transform { value ->
  if (predicate(value)) return@transform onNext(value)
}

fun <T> Publisher<T>.onEach(action: (T) -> Unit): Publisher<T> = transform { value ->
  action(value)
  return@transform onNext(value)
}


fun interface Subscriber<T> {
  fun onNext(value: T)
}


fun requestUser(): Publisher<User> {
  return Publisher {
    it.onNext(User("George"))
  }
}

fun main() {
  var cur = 0
  requestUser()
    .transform {
      onNext(User(it.name + " transf"))
      onNext(User(it.name))
    }
    .onEach { println("received:$it") }
    .map { ++cur }
    .filter { it % 2 == 0 }
    .subscribe { println("received:$it") }
}

data class User(val name: String)
