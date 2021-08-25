package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.isPixIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.reactivex.Completable

class PixIdIsUuidMiddleware : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): Completable {
    if (request.isPixIdNotAnUuid()) {
      return Completable.error(fieldIsNotAnUuidViolation(field = "pix_id"))
    }

    return this.checkNext(request)
  }

}
