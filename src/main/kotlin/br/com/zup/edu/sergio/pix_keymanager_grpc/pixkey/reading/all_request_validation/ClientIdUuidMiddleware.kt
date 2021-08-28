package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.all_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingAllRequest
import reactor.core.publisher.Mono

class ClientIdUuidMiddleware : RequestMiddleware<PixKeyReadingAllRequest>() {
  override fun check(request: PixKeyReadingAllRequest): Mono<PixKeyReadingAllRequest> {
    if (request.isClientIdNotAnUuid) {
      return Mono.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request = request)
  }
}
