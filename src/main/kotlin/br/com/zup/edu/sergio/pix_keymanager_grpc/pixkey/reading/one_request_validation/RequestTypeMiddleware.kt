package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.typeCanNotBeDetermined
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import io.grpc.Status
import io.reactivex.Completable

class RequestTypeMiddleware : RequestMiddleware<PixKeyReadingOneRequest>() {
  override fun check(request: PixKeyReadingOneRequest): Completable {
    if (request.typeCanNotBeDetermined) {
      return Completable.error(
        Status.INVALID_ARGUMENT
          .withDescription("request type can't be determined")
          .augmentDescription(
            """
              For a local pix key:
              - pix_id and client_id must be set
              - pix_key must not be set
              
              For an external pix key:
              - pix_key must be set
              - pix_id and client_id must not be set
            """.trimIndent()
          )
          .asRuntimeException()
      )
    }

    return this.checkNext(request = request)
  }
}
