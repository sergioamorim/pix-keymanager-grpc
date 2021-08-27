package br.com.zup.edu.sergio.pix_keymanager_grpc

import reactor.core.publisher.Mono

abstract class RequestMiddleware<T> {
  private var next: RequestMiddleware<T>? = null

  abstract fun check(request: T): Mono<T>

  fun checkNext(request: T): Mono<T> =
    next?.check(request) ?: Mono.just(request)

  fun linkWith(next: RequestMiddleware<T>): RequestMiddleware<T> {
    this.next = next
    return next
  }
}
