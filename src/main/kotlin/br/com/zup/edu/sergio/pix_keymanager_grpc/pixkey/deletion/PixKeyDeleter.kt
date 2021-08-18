package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.DeletePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.isDifferentFromNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Completable
import javax.inject.Inject

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
      .doOnError { error: Throwable ->
        if (error is HttpClientResponseException) {
          translatedError(error = error)?.let { translatedError: Throwable ->
            Completable.error(translatedError)
          }
        }
        Completable.complete()
      }
}

private fun translatedError(
  error: HttpClientResponseException
): StatusRuntimeException? {
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
