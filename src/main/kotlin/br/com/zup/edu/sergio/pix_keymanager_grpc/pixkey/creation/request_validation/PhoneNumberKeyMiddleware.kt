package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidPhoneNumberKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isPhoneNumberKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.rxjava3.core.Completable

class PhoneNumberKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isPhoneNumberKey && request.hasNotAValidPhoneNumberKey) {
      return Completable.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "must be a phone number (ex.: +55987654321)"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
