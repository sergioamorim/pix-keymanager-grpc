package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.lengthIsGreaterThan
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class KeyLengthMiddleware : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.lengthIsGreaterThan(maxLength = 77)) {
      return Completable.error(
        Status.INVALID_ARGUMENT
          .withDescription("key length is too big")
          .augmentDescription("the key length must be smaller or equal to 77")
          .asRuntimeException()
      )
    }

    return this.checkNext(request)
  }
}
