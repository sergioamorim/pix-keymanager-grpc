package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class ClientIdMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isClientIdNotAnUuid) {
      return Completable.error(
        Status.INVALID_ARGUMENT
          .withDescription("client id must be an UUID")
          .augmentDescription("example: 123e4567-e89b-12d3-a456-426614174000")
          .asRuntimeException()
      )
    }

    return this.checkNext(request)
  }

}
