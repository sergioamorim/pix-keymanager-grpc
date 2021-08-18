package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class ClientIdIsUuidMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): StatusRuntimeException? {
    if (request.isClientIdNotAnUuid()) {
      return Status.INVALID_ARGUMENT
        .withDescription("client id must be a valid UUID")
        .augmentDescription("example: 123e4567-e89b-12d3-a456-426614174000")
        .asRuntimeException()
    }

    return this.checkNext(request)
  }

}
