package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class RandomKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isRandomKey && request.key.isNotEmpty()) {
      return Completable.error(
        fieldViolation(
          field = "key",
          status = Status.INVALID_ARGUMENT,
          description = "must be empty (it will be generated afterwards)"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
