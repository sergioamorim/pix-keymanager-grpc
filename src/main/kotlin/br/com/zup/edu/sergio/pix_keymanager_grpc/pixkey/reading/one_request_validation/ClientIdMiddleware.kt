package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import reactor.core.publisher.Mono

class ClientIdMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Mono<PixKeyReadingOneRequest> {
    if (request.isLocal && request.isClientIdNotAnUuid) {
      return Mono.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request = request)
  }
}
