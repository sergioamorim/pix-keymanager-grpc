package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasInvalidAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class AccountTypeMiddleware : PixKeyRequestMiddleware() {

  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.hasInvalidAccountType()) {
      Status.INVALID_ARGUMENT
        .withDescription("account type is required")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

}
