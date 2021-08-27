package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import reactor.core.publisher.Mono

class ClientIdMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Mono<PixKeyCreationRequest> {
    if (request.isClientIdNotAnUuid) {
      return Mono.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request = request)
  }

}
