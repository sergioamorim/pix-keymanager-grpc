package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.hasNotAValidEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.isEmailKey
import io.grpc.Status
import io.grpc.StatusRuntimeException

class EmailKeyMiddleware : PixKeyRequestMiddleware() {

  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.isEmailKey() and pixKeyRequest.hasNotAValidEmailKey()) {
      Status.INVALID_ARGUMENT
        .withDescription("email key with invalid format")
        .augmentDescription("a key with the following format is expected: email@example.com")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

}
