package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientIdIsUuidMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientIdMatchesMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdExistsMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdNotBlankMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionServiceGrpc
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyDeletionEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyDeleter: PixKeyDeleter
) : PixKeyDeletionServiceGrpc.PixKeyDeletionServiceImplBase() {

  private val requestValidationChain: RequestMiddleware<PixKeyDeletionRequest> =
    ClientIdIsUuidMiddleware()

  init {
    this.requestValidationChain
      .linkWith(PixIdNotBlankMiddleware())
      .linkWith(PixIdExistsMiddleware(this.pixKeyRepository))
      .linkWith(ClientIdMatchesMiddleware(this.pixKeyRepository))
  }

  override fun deletePixKey(
    pixKeyDeletionRequest: PixKeyDeletionRequest,
    responseObserver: StreamObserver<Empty>
  ) {

    this.requestValidationChain.check(pixKeyDeletionRequest)
      .doOnComplete {
        this.proceedPixKeyDeletion(
          pixKey = this.pixKeyRepository.getById(
            pixId = pixKeyDeletionRequest.pixId
          ),
          responseObserver
        )
      }
      .doOnError { error: Throwable -> responseObserver.onError(error) }
      .subscribe()
  }

  private fun proceedPixKeyDeletion(
    pixKey: PixKey, responseObserver: StreamObserver<Empty>
  ) {
    this.pixKeyDeleter.deletePixKey(pixKey = pixKey)
      .doOnComplete {
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
      }
      .doOnError { error: Throwable -> responseObserver.onError(error) }
      .observeOn(Schedulers.io())
      .subscribe()
  }
}
