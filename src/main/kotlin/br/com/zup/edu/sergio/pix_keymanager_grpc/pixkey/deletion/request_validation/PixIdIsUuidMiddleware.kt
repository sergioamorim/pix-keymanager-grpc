package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.isPixIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import reactor.core.publisher.Mono

class PixIdIsUuidMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Mono<PixKeyDeletionRequest> {
    if (request.isPixIdNotAnUuid) {
      return Mono.error(fieldIsNotAnUuidViolation(field = "pix_id"))
    }

    return this.checkNext(request)
  }

}
