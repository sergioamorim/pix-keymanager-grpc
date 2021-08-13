package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.StatusRuntimeException

abstract class PixKeyCreationRequestMiddleware {
  private var next: PixKeyCreationRequestMiddleware? = null

  fun linkWith(next: PixKeyCreationRequestMiddleware): PixKeyCreationRequestMiddleware {
    this.next = next
    return next
  }

  abstract fun check(pixKeyCreationRequest: PixKeyCreationRequest): StatusRuntimeException?

  protected fun checkNext(pixKeyCreationRequest: PixKeyCreationRequest): StatusRuntimeException? {
    return this.next?.check(pixKeyCreationRequest)
  }
}
