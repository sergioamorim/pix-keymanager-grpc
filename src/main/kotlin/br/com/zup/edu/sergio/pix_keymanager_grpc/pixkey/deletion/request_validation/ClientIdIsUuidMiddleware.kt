package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.reactivex.Completable

class ClientIdIsUuidMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (request.isClientIdNotAnUuid()) {
      return Completable.error(
        fieldViolation(
          field = "clientId",
          status = Status.INVALID_ARGUMENT,
          description = "must be an UUID (ex.: 123e4567-e89b-12d3-a456-426614174000)"
        )
      )
    }

    return this.checkNext(request)
  }
}
