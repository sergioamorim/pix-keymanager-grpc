package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.DeletePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.isDifferentFromNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Completable
import jakarta.inject.Inject

class PixKeyDeleter @Inject constructor(
  private val bcbClient: BcbClient,
  private val pixKeyRepository: PixKeyRepository
) {

  fun deletePixKey(pixKey: PixKey): Completable =
    this.deleteOnBcb(pixKey)
      .doOnComplete { this.pixKeyRepository.delete(pixKey) }

  private fun deleteOnBcb(pixKey: PixKey): Completable =
    this.bcbClient
      .deletePixKey(
        key = pixKey.key,
        deletePixKeyRequest = DeletePixKeyRequest(
          key = pixKey.key, participant = pixKey.participant
        )
      )
      .onErrorResumeNext { error: Throwable ->
        translatedError(error = error)?.let(Completable::error)
          ?: Completable.complete()
      }
}

private fun translatedError(error: Throwable): Throwable? =
  when (error) {
    is HttpClientResponseException -> responseError(error = error)

    is HttpClientException -> Status.UNAVAILABLE
      .withDescription("bcb pix key deletion service unavailable")
      .augmentDescription(
        "unable to delete the pix key because the bcb system's " +
        "pix key deletion service isn't responding"
      )
      .asRuntimeException()

    else -> Status.INTERNAL
      .withDescription("internal problem on pix key deletion")
      .augmentDescription(
        "unexpected behavior when connecting to the bcb system's " +
        "pix key deletion service"
      )
      .asRuntimeException()
  }

private fun responseError(
  error: HttpClientResponseException
): Throwable? {
  if (error.isDifferentFromNotFound()) {
    return Status.UNAVAILABLE
      .withDescription("bcb pix key deletion service unavailable")
      .augmentDescription(
        "can't delete the pix key because the bcb system is " +
        "returning an unknown response"
      )
      .asRuntimeException()
  }

  return null
}
