package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import io.reactivex.Completable

class ClientIdMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Completable {

    return this.checkNext(request)
  }
}
