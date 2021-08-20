package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isEmailKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class EmailKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isEmailKey and request.hasNotAValidEmailKey) {
      return Completable.error(
        Status.INVALID_ARGUMENT
          .withDescription("email key with invalid format")
          .augmentDescription("a key with the following format is expected: email@example.com")
          .asRuntimeException()
      )
    }

    return this.checkNext(request)
  }
}
