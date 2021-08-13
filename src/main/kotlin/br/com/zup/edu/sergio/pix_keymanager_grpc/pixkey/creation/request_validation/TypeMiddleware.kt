package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasInvalidType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class TypeMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): StatusRuntimeException? {
    if (request.hasInvalidType()) {
      return Status.INVALID_ARGUMENT
        .withDescription("type is required")
        .asRuntimeException()
    }

    return this.checkNext(request)
  }

}
