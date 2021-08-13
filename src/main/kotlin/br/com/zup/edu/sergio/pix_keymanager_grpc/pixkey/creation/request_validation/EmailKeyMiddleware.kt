package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class EmailKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): StatusRuntimeException? =
    if (request.isEmailKey() and request.hasNotAValidEmailKey()) {
      Status.INVALID_ARGUMENT
        .withDescription("email key with invalid format")
        .augmentDescription("a key with the following format is expected: email@example.com")
        .asRuntimeException()
    } else {
      this.checkNext(request)
    }

}
