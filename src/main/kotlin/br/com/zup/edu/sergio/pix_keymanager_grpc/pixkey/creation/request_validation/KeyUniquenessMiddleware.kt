package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isNotRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class KeyUniquenessMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isNotRandomKey() and this.pixKeyRepository.existsByKey(request.key)) {
      return Completable.error(
        Status.ALREADY_EXISTS
          .withDescription("key must be unique")
          .augmentDescription("the key already exists in the database")
          .asRuntimeException()
      )
    }

    return this.checkNext(request)
  }
}
