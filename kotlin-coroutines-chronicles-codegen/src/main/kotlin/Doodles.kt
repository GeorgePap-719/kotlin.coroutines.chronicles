package coroutines.chronicles.codegen

interface Continuation<T> {
  fun await(resumer: Resumer<T>)
}

fun <T> createContinuation(block: Resumer<T>.() -> Unit): Continuation<T> {
  return object: Continuation<T> {
    override fun await(resumer: Resumer<T>) {
      resumer.block()
    }
  }
}

fun interface Resumer<T> {
  fun resume(value: T)
}

//fun <T, R> Continuation<T>.map(transform: (value: T) -> R): Continuation<R> {
//    var t: T? = null
//    await { t = it }
//    return object : Continuation<R> {
//        override fun await(resumer: Resumer<R>) {
//            resumer.resume(transform(t!!))
//        }
//    }
//}

//fun <T> Continuation<T>.onEach(action: (value: T) -> Unit): Continuation<T> {
//    action()
//    await {
//        action(it)
//        resume(it)
//    }
//    return this
//}

fun <T, R> Continuation<T>.transform(
  transform: Resumer<R>.(value: T) -> Unit
): Continuation<R> {
  await {

  }
  return object : Continuation<R> {
    override fun await(resumer: Resumer<R>) {
      this@transform.await {
//                resumer.resume()
      }
    }
  }
}

//var c: Continuation<User> = object : Continuation<User> {
//    private var _resumer: Resumer<User>? = null
//
//    @Volatile
//    private var exit = false
//
//    override fun await(resumer: Resumer<User>) {
//        _resumer = resumer
//        // Lets cheat lol
//        while (true) {
//            if (exit) return
//        }
//    }
//
//    override fun resume(value: User) {
//        _resumer!!.resume(value)
//        exit = true
//    }
//}

fun main() {
//    val t = Thread {
//        requestUserAsync().await {
//            println("Received:$it")
//        }
//    }
//    t.start()
//    println("resuming")
//    println(t.state)
//    Thread.sleep(100)
//    c.resume(User("George"))
//    t.join()
//    println("exiting")
  //
//    val t = Thread {
//        requestUserAsync()
//            .onEach { println(it) }
//            .map { User("Kostas") }
//            .await {
//                println("Received:$it")
//            }
//    }
//    t.start()
//    println("resuming")
//    println(t.state)
//    Thread.sleep(100)
//    //c.map { User("Kostas") }
//    c.resume(User("George"))
//    t.join()
//    println("exiting")
  // 3--- Try

  var c = createContinuation<User> {

  }
}


//fun requestUserAsync(): Continuation<User> {
//    return c
//}

data class User(val name: String)
