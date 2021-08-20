package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.reactivex.Completable

class PixIdExistsMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (!this.pixKeyRepository.existsById(request.pixId)) {
      return Completable.error(
        fieldViolation(
          field = "pixId",
          status = Status.NOT_FOUND,
          description = "pixId must be the id of an existing pix key"
        )
      )
    }

    return this.checkNext(request)
  }

}
