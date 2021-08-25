package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldIsNotAnUuidViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isLocal
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.isPixIdNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import io.reactivex.Completable

class PixIdMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Completable {
    if (request.isLocal() and request.isPixIdNotAnUuid()) {
      return Completable.error(fieldIsNotAnUuidViolation(field = "pix_id"))
    }

    return this.checkNext(request)
  }
}
