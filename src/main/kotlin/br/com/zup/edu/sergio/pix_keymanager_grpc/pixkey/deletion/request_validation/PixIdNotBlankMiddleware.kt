package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.reactivex.Completable

class PixIdNotBlankMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (request.pixId.isBlank()) {
      return Completable.error(
        fieldViolation(
          field = "pixId",
          status = Status.INVALID_ARGUMENT,
          description = "must not be blank"
        )
      )
    }

    return this.checkNext(request)
  }

}
