package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.lengthIsGreaterThan
import io.grpc.Status
import io.grpc.StatusRuntimeException

class KeySizeMiddleware : PixKeyRequestMiddleware() {
  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.lengthIsGreaterThan(maxLength = 77)) {
      Status.INVALID_ARGUMENT
        .withDescription("key length is too big")
        .augmentDescription("the key length must be smaller or equal to 77")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }
}
