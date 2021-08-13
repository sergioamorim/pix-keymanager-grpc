package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class RandomKeyMiddleware : PixKeyCreationRequestMiddleware() {

  override fun check(pixKeyCreationRequest: PixKeyCreationRequest): StatusRuntimeException? =
    if (pixKeyCreationRequest.isRandomKey() and pixKeyCreationRequest.key.isNotEmpty()) {
      Status.INVALID_ARGUMENT
        .withDescription("random key must be empty")
        .augmentDescription("the random key is generated afterwards")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyCreationRequest)
    }

}
