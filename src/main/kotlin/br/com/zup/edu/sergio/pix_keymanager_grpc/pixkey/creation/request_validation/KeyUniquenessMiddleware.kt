package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.isNotRandomKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.reactivex.Completable

class KeyUniquenessMiddleware(
  private val pixKeyRepository: PixKeyRepository
) : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): Completable {
    if (request.isNotRandomKey && this.pixKeyRepository.existsByKey(request.key)) {
      return Completable.error(
        fieldViolation(
          field = "key",
          status = Status.ALREADY_EXISTS,
          description = "must be unique"
        )
      )
    }

    return this.checkNext(request = request)
  }
}
