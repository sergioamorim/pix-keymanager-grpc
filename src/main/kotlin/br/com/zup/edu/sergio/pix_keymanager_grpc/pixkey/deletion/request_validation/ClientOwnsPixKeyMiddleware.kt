package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.clientDoesNotOwnPixKeyViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import reactor.core.publisher.Mono

class ClientOwnsPixKeyMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Mono<PixKeyDeletionRequest> {
    if (!this.pixKeyRepository.existsByIdAndClientId(request.pixId, request.clientId)) {
      return Mono.error(clientDoesNotOwnPixKeyViolation())
    }

    return this.checkNext(request = request)
  }

}
