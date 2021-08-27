package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixIdNotFoundViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import jakarta.inject.Inject
import reactor.core.publisher.Mono

class PixIdExistsMiddleware @Inject constructor(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyReadingOneRequest>() {

  override fun check(request: PixKeyReadingOneRequest): Mono<PixKeyReadingOneRequest> {
    if (request.isLocal && !this.pixKeyRepository.existsById(request.pixId)) {
      return Mono.error(pixIdNotFoundViolation())
    }

    return this.checkNext(request = request)
  }
}
