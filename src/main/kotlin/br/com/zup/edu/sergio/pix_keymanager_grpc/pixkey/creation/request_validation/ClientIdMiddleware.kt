package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.reactivex.rxjava3.core.Completable

class ClientIdMiddleware : RequestMiddleware<PixKeyCreationRequest>() {

  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isClientIdNotAnUuid) {
      return Completable.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request = request)
  }

}
