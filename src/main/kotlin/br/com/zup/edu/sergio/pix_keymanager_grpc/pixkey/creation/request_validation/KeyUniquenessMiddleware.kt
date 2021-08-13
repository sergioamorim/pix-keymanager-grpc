package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isNotRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException

class KeyUniquenessMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : PixKeyCreationRequestMiddleware() {
  override fun check(pixKeyCreationRequest: PixKeyCreationRequest): StatusRuntimeException? =
    if (pixKeyCreationRequest.isNotRandomKey() and this.pixKeyRepository.existsByKey(
        pixKeyCreationRequest.key
      )
    ) {
      Status.ALREADY_EXISTS
        .withDescription("key must be unique")
        .augmentDescription("the key already exists in the database")
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyCreationRequest)
    }
}
