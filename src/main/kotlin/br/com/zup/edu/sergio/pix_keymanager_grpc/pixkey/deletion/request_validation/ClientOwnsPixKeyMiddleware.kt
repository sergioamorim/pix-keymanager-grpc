package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.clientDoesNotOwnPixKeyViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.reactivex.rxjava3.core.Completable

class ClientOwnsPixKeyMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (!this.pixKeyRepository.existsByIdAndClientId(request.pixId, request.clientId)) {
      return Completable.error(clientDoesNotOwnPixKeyViolation())
    }

    return this.checkNext(request = request)
  }

}
