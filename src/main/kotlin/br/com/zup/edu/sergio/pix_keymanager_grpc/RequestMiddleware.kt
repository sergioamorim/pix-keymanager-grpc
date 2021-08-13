package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.grpc.StatusRuntimeException

abstract class RequestMiddleware<T> {
  private var next: RequestMiddleware<T>? = null

  abstract fun check(request: T): StatusRuntimeException?

  fun checkNext(request: T): StatusRuntimeException? =
    next?.check(request)

  fun linkWith(next: RequestMiddleware<T>): RequestMiddleware<T> {
    this.next = next
    return next
  }
}
