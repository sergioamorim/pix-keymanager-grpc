package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasInvalidAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class AccountTypeMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): StatusRuntimeException? =
    if (request.hasInvalidAccountType()) {
      Status.INVALID_ARGUMENT
        .withDescription("account type is required")
        .asRuntimeException()
    } else {
      this.checkNext(request)
    }

}
