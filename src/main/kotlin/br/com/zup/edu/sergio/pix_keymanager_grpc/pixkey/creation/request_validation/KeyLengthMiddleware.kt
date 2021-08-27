package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.lengthIsGreaterThan
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import reactor.core.publisher.Mono

class KeyLengthMiddleware : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): Mono<PixKeyCreationRequest> {
    if (request.lengthIsGreaterThan(maxLength = 77)) {
      return Mono.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "key length must be smaller or equal to 77"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
