package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.Either
import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyCreationEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val pixKeyCreator: PixKeyCreator
) : PixKeyCreationServiceGrpc.PixKeyCreationServiceImplBase() {

  private val requestValidationChain: RequestMiddleware<PixKeyCreationRequest> =
    KeyUniquenessMiddleware(this.pixKeyRepository)

  init {
    this.requestValidationChain
      .linkWith(ClientIdMiddleware())
      .linkWith(EmailKeyMiddleware())
      .linkWith(PhoneNumberKeyMiddleware())
      .linkWith(CpfKeyMiddleware())
      .linkWith(KeyLengthMiddleware())
      .linkWith(RandomKeyMiddleware())
      .linkWith(AccountTypeMiddleware())
      .linkWith(TypeMiddleware())
  }

  override fun createPixKey(
    pixKeyCreationRequest: PixKeyCreationRequest,
    responseObserver: StreamObserver<PixKeyCreationResponse>
  ) {
    this.requestValidationChain.check(pixKeyCreationRequest)
      .doOnComplete {
        this.pixKeyCreator.createPixKey(pixKeyCreationRequest)
          .let { result: Either<StatusRuntimeException, PixKeyCreationResponse> ->
            when (result) {
              is Either.Right<PixKeyCreationResponse> ->
                responseObserver.onNext(result.right)

              is Either.Left<StatusRuntimeException> ->
                responseObserver.onError(result.left)
            }
          }
        responseObserver.onCompleted()
      }
      .doOnError { error: Throwable ->
        responseObserver.onError(error)
      }
      .subscribe()
  }
}
