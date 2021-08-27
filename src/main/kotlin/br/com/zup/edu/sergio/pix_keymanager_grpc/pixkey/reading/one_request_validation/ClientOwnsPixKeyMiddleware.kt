package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.clientDoesNotOwnPixKeyViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import reactor.core.publisher.Mono

class ClientOwnsPixKeyMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyReadingOneRequest>() {

  override fun check(request: PixKeyReadingOneRequest): Mono<PixKeyReadingOneRequest> {
    if (
      request.isLocal
      && !this.pixKeyRepository.existsByIdAndClientId(
        id = request.pixId, clientId = request.clientId
      )
    ) {
      return Mono.error(clientDoesNotOwnPixKeyViolation())
    }

    return this.checkNext(request = request)
  }
}
