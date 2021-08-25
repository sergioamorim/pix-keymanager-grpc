package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.reactivex.Completable

class ClientIdIsUuidMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (request.isClientIdNotAnUuid()) {
      return Completable.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request)
  }
}
