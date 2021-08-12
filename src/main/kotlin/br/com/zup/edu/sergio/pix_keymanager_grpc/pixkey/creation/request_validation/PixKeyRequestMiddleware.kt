package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import io.grpc.StatusRuntimeException

abstract class PixKeyRequestMiddleware {
  private var next: PixKeyRequestMiddleware? = null

  fun linkWith(next: PixKeyRequestMiddleware): PixKeyRequestMiddleware {
    this.next = next
    return next
  }

  abstract fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException?

  protected fun checkNext(pixKeyRequest: PixKeyRequest): StatusRuntimeException? {
    return this.next?.check(pixKeyRequest)
  }
}
