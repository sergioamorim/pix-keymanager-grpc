package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.completeOnNext
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientIdIsUuidMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientOwnsPixKeyMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdExistsMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdIsUuidMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionServiceGrpc
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import io.reactivex.rxjava3.core.Scheduler
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PixKeyDeletionEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyDeleter: PixKeyDeleter,
  private val scheduler: Scheduler
) : PixKeyDeletionServiceGrpc.PixKeyDeletionServiceImplBase() {

  private val requestValidationChain: RequestMiddleware<PixKeyDeletionRequest> =
    ClientIdIsUuidMiddleware()

  init {
    this.requestValidationChain
      .linkWith(PixIdIsUuidMiddleware())
      .linkWith(PixIdExistsMiddleware(pixKeyRepository = this.pixKeyRepository))
      .linkWith(ClientOwnsPixKeyMiddleware(pixKeyRepository = this.pixKeyRepository))
  }

  override fun deletePixKey(
    pixKeyDeletionRequest: PixKeyDeletionRequest,
    responseObserver: StreamObserver<Empty>
  ) {

    this.requestValidationChain.check(request = pixKeyDeletionRequest)
      .observeOn(this.scheduler)
      .subscribe(
        {
          this.proceedPixKeyDeletion(
            pixKey = this.pixKeyRepository.getById(
              id = pixKeyDeletionRequest.pixId
            ),
            responseObserver = responseObserver
          )
        },
        responseObserver::onError
      )
  }

  private fun proceedPixKeyDeletion(
    pixKey: PixKey, responseObserver: StreamObserver<Empty>
  ) {
    this.pixKeyDeleter.deletePixKey(pixKey = pixKey)
      .observeOn(this.scheduler)
      .subscribe(
        { responseObserver.completeOnNext(response = Empty.getDefaultInstance()) },
        responseObserver::onError
      )
  }
}
