package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.hasNotAValidCpfKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isCpfKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class CpfKeyMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isCpfKey and request.hasNotAValidCpfKey) {
      return Completable.error(
        Status.INVALID_ARGUMENT
          .withDescription("cpf key with invalid format")
          .augmentDescription("the required format is 11 numbers - ex.: 12345678901")
          .asRuntimeException()
      )
    }

    return this.checkNext(request)
  }
}
