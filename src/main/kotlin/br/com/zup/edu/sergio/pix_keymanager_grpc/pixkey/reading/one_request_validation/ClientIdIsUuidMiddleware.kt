package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isClientIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import io.reactivex.Completable

class ClientIdIsUuidMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Completable {
    if (request.isLocal() and request.isClientIdNotAnUuid()) {
      return Completable.error(fieldIsNotAnUuidViolation(field = "client_id"))
    }

    return this.checkNext(request = request)
  }
}
