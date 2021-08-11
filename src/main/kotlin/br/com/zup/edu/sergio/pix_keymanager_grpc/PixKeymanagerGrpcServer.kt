package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.asPixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.request_validation.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeymanagerGrpcServer @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val erpClient: ErpClient
) : PixKeyServiceGrpc.PixKeyServiceImplBase() {

  private val pixKeyRequestMiddleware: PixKeyRequestMiddleware =
    KeyUniquenessMiddleware(this.pixKeyRepository)

  init {
    this.pixKeyRequestMiddleware
      .linkWith(ClientIdMiddleware(this.erpClient))
      .linkWith(EmailKeyMiddleware())
      .linkWith(PhoneNumberKeyMiddleware())
      .linkWith(CpfKeyMiddleware())
      .linkWith(KeySizeMiddleware())
      .linkWith(RandomKeyMiddleware())
      .linkWith(AccountTypeMiddleware())
  }

  override fun createPixKey(
    pixKeyRequest: PixKeyRequest, responseObserver: StreamObserver<PixKeyResponse>
  ) {

    this.pixKeyRequestMiddleware.check(pixKeyRequest)?.let { error: StatusRuntimeException ->
      responseObserver.onError(error)
      return
    }

    responseObserver.onNext(
      PixKeyResponse
        .newBuilder()
        .setPixId(this.pixKeyRepository.save(pixKeyRequest.asPixKey()).id)
        .build()
    )
    responseObserver.onCompleted()
  }
}
