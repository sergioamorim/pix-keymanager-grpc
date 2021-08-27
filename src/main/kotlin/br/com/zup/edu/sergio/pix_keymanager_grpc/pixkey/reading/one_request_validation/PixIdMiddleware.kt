package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isPixIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import reactor.core.publisher.Mono

class PixIdMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Mono<PixKeyReadingOneRequest> {
    if (request.isLocal && request.isPixIdNotAnUuid) {
      return Mono.error(fieldIsNotAnUuidViolation(field = "pix_id"))
    }

    return this.checkNext(request = request)
  }
}
