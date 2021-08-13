package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class PixIdExistsMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyDeletionRequest>() {

  override fun check(request: PixKeyDeletionRequest): StatusRuntimeException? {
    if (!this.pixKeyRepository.existsById(request.pixId)) {
      return Status.NOT_FOUND
        .withDescription("pix key not found")
        .augmentDescription("unable to find a pix key with this id")
        .asRuntimeException()
    }

    return this.checkNext(request)
  }

}
