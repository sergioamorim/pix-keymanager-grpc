package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyCreationEndpoint @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val erpClient: ErpClient
) : PixKeyCreationServiceGrpc.PixKeyCreationServiceImplBase() {

  private val requestValidationChain: RequestMiddleware<PixKeyCreationRequest> =
    KeyUniquenessMiddleware(this.pixKeyRepository)

  init {
    this.requestValidationChain
      .linkWith(ClientIdMiddleware(this.erpClient))
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
      ?.let { error: StatusRuntimeException ->
        responseObserver.onError(error)
        return
      }

    responseObserver.onNext(
      PixKeyCreationResponse
        .newBuilder()
        .setPixId(this.pixKeyRepository.save(pixKeyCreationRequest.asPixKey()).id)
        .build()
    )
    responseObserver.onCompleted()
  }
}
