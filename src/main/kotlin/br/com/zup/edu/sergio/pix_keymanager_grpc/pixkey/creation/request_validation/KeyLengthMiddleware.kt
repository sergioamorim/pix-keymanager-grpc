package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.lengthIsGreaterThan
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class KeyLengthMiddleware : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.lengthIsGreaterThan(maxLength = 77)) {
      return Completable.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "key length must be smaller or equal to 77"
        )
      )
    }

    return this.checkNext(request)
  }
}
