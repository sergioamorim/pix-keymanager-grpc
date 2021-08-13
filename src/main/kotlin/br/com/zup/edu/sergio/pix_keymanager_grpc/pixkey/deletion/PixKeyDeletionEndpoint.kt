package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientIdMatchesMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.ClientIdNotBlankMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdExistsMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation.PixIdNotBlankMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionServiceGrpc
import com.google.protobuf.Empty
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyDeletionEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository
) : PixKeyDeletionServiceGrpc.PixKeyDeletionServiceImplBase() {

  private val requestValidationChain: RequestMiddleware<PixKeyDeletionRequest> =
    ClientIdNotBlankMiddleware()

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
      ?.let { error: StatusRuntimeException ->
        responseObserver.onError(error)
        return
      }

    this.pixKeyRepository.deleteById(pixKeyDeletionRequest.pixId)
    responseObserver.onNext(Empty.getDefaultInstance())
    responseObserver.onCompleted()
  }
}
