package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.grpc.stub.StreamObserver
import kotlin.concurrent.thread

class ResponseObserverMock<T>(private val lifespanMillis: Long = 1000) {

  init {
    thread {
      Thread.sleep(this.lifespanMillis)
      this.timedOut = true
    }
  }

  private val observedValuesBuilder: MutableList<T> = mutableListOf()
  val observedValues: Collection<T>
    get() = this.observedValuesBuilder

  var error: Throwable? = null
    private set

  var completed: Boolean = false
    private set

  var timedOut: Boolean = false

  fun waitForIt() {
    while (this.hasNotEnded()) {
      Thread.sleep(1)
    }
  }

  private fun hasNotEnded(): Boolean {
    return !this.completed && null == this.error && !this.timedOut
  }

  val responseObserver: StreamObserver<T> = object : StreamObserver<T> {
    override fun onNext(value: T) {
      this@ResponseObserverMock.observedValuesBuilder.add(element = value)
    }

    override fun onError(error: Throwable) {
      this@ResponseObserverMock.error = error
    }

    override fun onCompleted() {
      this@ResponseObserverMock.completed = true
    }
  }
}
