package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import reactor.core.publisher.Mono

class EmailKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Mono<PixKeyCreationRequest> {
    if (request.isEmailKey && request.hasNotAValidEmailKey) {
      return Mono.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "must be an email (ex.: email@example.com)"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
