package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.completeOnNext
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingServiceGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyReadingEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyReader: PixKeyReader
) : PixKeyReadingServiceGrpc.PixKeyReadingServiceImplBase() {

  private val requestOneValidationChain: RequestMiddleware<PixKeyReadingOneRequest> =
    RequestTypeMiddleware()

  init {
    this.requestOneValidationChain
      .linkWith(ClientIdMiddleware())
      .linkWith(PixIdMiddleware())
      .linkWith(PixIdExistsMiddleware(pixKeyRepository = this.pixKeyRepository))
      .linkWith(ClientOwnsPixKeyMiddleware(pixKeyRepository = this.pixKeyRepository))
  }

  override fun readOnePixKey(
    pixKeyReadingOneRequest: PixKeyReadingOneRequest,
    responseObserver: StreamObserver<PixKeyReadingOneResponse>
  ) {
    this.requestOneValidationChain
      .check(request = pixKeyReadingOneRequest)
      .subscribe(
        {
          this.proceedPixKeyReadingOne(
            pixKeyReadingOneRequest = pixKeyReadingOneRequest,
            responseObserver = responseObserver
          )
        },
        responseObserver::onError
      )

  }

  private fun proceedPixKeyReadingOne(
    pixKeyReadingOneRequest: PixKeyReadingOneRequest,
    responseObserver: StreamObserver<PixKeyReadingOneResponse>
  ) {
    this.pixKeyReader
      .readOnePixKey(pixKeyReadingOneRequest = pixKeyReadingOneRequest)
      .subscribe(responseObserver::completeOnNext, responseObserver::onError)
  }
}
