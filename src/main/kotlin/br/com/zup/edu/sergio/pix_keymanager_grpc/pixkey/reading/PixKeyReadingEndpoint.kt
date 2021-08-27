package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.completeOnNext
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingServiceGrpc
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.scheduler.Scheduler

@Singleton
class PixKeyReadingEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyReader: PixKeyReader,
  private val scheduler: Scheduler
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
      .subscribeOn(this.scheduler)
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
      .subscribeOn(this.scheduler)
      .subscribe(responseObserver::completeOnNext, responseObserver::onError)
  }
}
