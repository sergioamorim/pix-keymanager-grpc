package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Assertions
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

  private var error: Throwable? = null

  private var completed: Boolean = false

  private var timedOut: Boolean = false

  fun waitForIt() {
    while (this.hasNotEnded()) {
      Thread.sleep(1)
    }
  }

  fun assertFieldViolation(field: String, status: Status) {
    this.assertStatusRuntimeException()
    with(this.error as StatusRuntimeException) {
      this.assertIsFieldViolation(field = field, status = status)
    }
  }

  fun assertStatus(status: Status) {
    this.assertStatusRuntimeException()
    with(this.error as StatusRuntimeException) {
      this.assertStatus(status = status)
    }
  }

  private fun assertStatusRuntimeException() {
    Assertions.assertTrue(
      this.error is StatusRuntimeException,
      "a StatusRuntimeError should be observed but it was not"
    )
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
