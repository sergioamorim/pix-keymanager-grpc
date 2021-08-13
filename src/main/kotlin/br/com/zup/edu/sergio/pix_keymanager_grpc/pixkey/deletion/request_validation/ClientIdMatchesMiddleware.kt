package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class ClientIdMatchesMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): StatusRuntimeException? {
    if (!this.pixKeyRepository.existsByIdAndClientId(request.pixId, request.clientId)) {
      return Status.PERMISSION_DENIED
        .withDescription("pix key does not belong to the client")
        .augmentDescription(
          "a pix key with this id was found, but the client id does not match"
        )
        .asRuntimeException()
    }

    return this.checkNext(request)
  }

}
