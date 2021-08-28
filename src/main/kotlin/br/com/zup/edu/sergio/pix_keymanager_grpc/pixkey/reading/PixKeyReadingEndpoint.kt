package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.completeOnNext
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.all_request_validation.ClientIdExistsMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.all_request_validation.ClientIdUuidMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.one_request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.*
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.scheduler.Scheduler

@Singleton
class PixKeyReadingEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyReader: PixKeyReader,
  private val erpClient: ErpClient,
  private val scheduler: Scheduler
) : PixKeyReadingServiceGrpc.PixKeyReadingServiceImplBase() {

  private val requestOneValidationChain: RequestMiddleware<PixKeyReadingOneRequest> =
    RequestTypeMiddleware()

  private val requestAllValidationChain: RequestMiddleware<PixKeyReadingAllRequest> =
    ClientIdUuidMiddleware()

  init {
    this.requestOneValidationChain
      .linkWith(ClientIdMiddleware())
      .linkWith(PixIdMiddleware())
      .linkWith(PixIdExistsMiddleware(pixKeyRepository = this.pixKeyRepository))
      .linkWith(ClientOwnsPixKeyMiddleware(pixKeyRepository = this.pixKeyRepository))

    this.requestAllValidationChain
      .linkWith(ClientIdExistsMiddleware(erpClient = this.erpClient))
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

  override fun readAllPixKeys(
    pixKeyReadingAllRequest: PixKeyReadingAllRequest,
    responseObserver: StreamObserver<PixKeyReadingAllResponse>
  ) {
    this.requestAllValidationChain
      .check(request = pixKeyReadingAllRequest)
      .subscribeOn(this.scheduler)
      .subscribe(
        {
          this.proceedPixKeyReadingAll(
            pixKeyReadingAllRequest = pixKeyReadingAllRequest,
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

  private fun proceedPixKeyReadingAll(
    pixKeyReadingAllRequest: PixKeyReadingAllRequest,
    responseObserver: StreamObserver<PixKeyReadingAllResponse>
  ) {
    val pixKeyReadingAllResponseBuilder: PixKeyReadingAllResponse.Builder =
      PixKeyReadingAllResponse.newBuilder()

    this.pixKeyReader
      .readAllPixKeys(clientId = pixKeyReadingAllRequest.clientId)
      .subscribeOn(this.scheduler)
      .doAfterTerminate {
        responseObserver.completeOnNext(pixKeyReadingAllResponseBuilder.build())
      }
      .subscribe(pixKeyReadingAllResponseBuilder::addPixKeys, responseObserver::onError)
  }
}
