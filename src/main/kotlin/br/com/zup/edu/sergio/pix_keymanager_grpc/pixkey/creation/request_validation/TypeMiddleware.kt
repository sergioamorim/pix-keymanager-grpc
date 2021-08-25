package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasInvalidType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class TypeMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.hasInvalidType) {
      return Completable.error(
        fieldViolation(
          field = "type",
          status = Status.INVALID_ARGUMENT,
          description = "type is required"
        )
      )
    }

    return this.checkNext(request = request)
  }

}
