package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasInvalidAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import reactor.core.publisher.Mono

class AccountTypeMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Mono<PixKeyCreationRequest> {
    if (request.hasInvalidAccountType) {
      return Mono.error(
        fieldViolation(
          field = "account_type",
          status = Status.INVALID_ARGUMENT,
          description = "account_type is required"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
