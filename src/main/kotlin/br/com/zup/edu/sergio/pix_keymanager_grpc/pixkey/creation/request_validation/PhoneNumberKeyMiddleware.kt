package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidPhoneNumberKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isPhoneNumberKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class PhoneNumberKeyMiddleware : PixKeyRequestMiddleware() {

  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (pixKeyRequest.isPhoneNumberKey() and pixKeyRequest.hasNotAValidPhoneNumberKey()) {
      Status.INVALID_ARGUMENT
        .withDescription("phone number key with invalid format")
        .augmentDescription(
          "the required format is plus sign, country code and phone number - ex: +5511999999999"
        )
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

}
