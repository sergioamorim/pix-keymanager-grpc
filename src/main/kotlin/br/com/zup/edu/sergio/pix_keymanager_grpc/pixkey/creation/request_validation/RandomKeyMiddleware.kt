package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class RandomKeyMiddleware : PixKeyRequestMiddleware() {

  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.isRandomKey() and pixKeyRequest.key.isNotEmpty()) {
      Status.INVALID_ARGUMENT
        .withDescription("random key must be empty")
        .augmentDescription("the random key is generated afterwards")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

}
