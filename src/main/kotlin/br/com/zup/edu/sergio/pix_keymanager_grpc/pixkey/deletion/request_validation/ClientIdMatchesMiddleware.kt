package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.reactivex.Completable

class ClientIdMatchesMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (!this.pixKeyRepository.existsByIdAndClientId(request.pixId, request.clientId)) {
      return Completable.error(
        fieldViolation(
          field = "pixId",
          status = Status.PERMISSION_DENIED,
          description = "pixId must be of a pix key which belongs to the client"
        )
      )
    }

    return this.checkNext(request)
  }

}
