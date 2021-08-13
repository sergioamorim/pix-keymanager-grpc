package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class PixIdNotBlankMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): StatusRuntimeException? {
    if (request.pixId.isBlank()) {
      return Status.INVALID_ARGUMENT
        .withDescription("pix id must be set")
        .augmentDescription("the pix id can't be null, empty or blank")
        .asRuntimeException()
    }

    return this.checkNext(request)
  }

}
