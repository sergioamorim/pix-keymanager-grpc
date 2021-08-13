package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class EmailKeyMiddleware : PixKeyCreationRequestMiddleware() {

  override fun check(pixKeyCreationRequest: PixKeyCreationRequest): StatusRuntimeException? =
    if (pixKeyCreationRequest.isEmailKey() and pixKeyCreationRequest.hasNotAValidEmailKey()) {
      Status.INVALID_ARGUMENT
        .withDescription("email key with invalid format")
        .augmentDescription("a key with the following format is expected: email@example.com")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyCreationRequest)
    }

}
