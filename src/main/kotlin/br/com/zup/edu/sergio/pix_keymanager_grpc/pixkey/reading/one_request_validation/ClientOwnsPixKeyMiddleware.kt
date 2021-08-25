package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.clientDoesNotOwnPixKeyViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import io.reactivex.Completable

class ClientOwnsPixKeyMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyReadingOneRequest>() {

  override fun check(request: PixKeyReadingOneRequest): Completable {
    if (!this.pixKeyRepository.existsByIdAndClientId(request.pixId, request.clientId)) {
      return Completable.error(clientDoesNotOwnPixKeyViolation())
    }

    return this.checkNext(request = request)
  }
}
