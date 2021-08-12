package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.hasNotAValidCpfKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.isCpfKey
import io.grpc.Status
import io.grpc.StatusRuntimeException

class CpfKeyMiddleware : PixKeyRequestMiddleware() {

  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.isCpfKey() and pixKeyRequest.hasNotAValidCpfKey()) {
      Status.INVALID_ARGUMENT
        .withDescription("cpf key with invalid format")
        .augmentDescription("the required format is 11 numbers - ex.: 12345678901")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

}
