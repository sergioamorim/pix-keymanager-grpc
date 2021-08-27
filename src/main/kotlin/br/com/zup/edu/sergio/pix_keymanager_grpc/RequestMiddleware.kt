package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.reactivex.rxjava3.core.Completable

abstract class RequestMiddleware<T> {
  private var next: RequestMiddleware<T>? = null

  abstract fun check(request: T): Completable

  fun checkNext(request: T): Completable =
    next?.check(request) ?: Completable.complete()

  fun linkWith(next: RequestMiddleware<T>): RequestMiddleware<T> {
    this.next = next
    return next
  }
}
