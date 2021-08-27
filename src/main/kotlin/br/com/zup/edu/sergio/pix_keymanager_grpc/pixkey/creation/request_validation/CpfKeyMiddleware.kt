package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidCpfKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isCpfKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import reactor.core.publisher.Mono

class CpfKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Mono<PixKeyCreationRequest> {
    if (request.isCpfKey && request.hasNotAValidCpfKey) {
      return Mono.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "must be a CPF (ex.: 12345678901)"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
