package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.DeletePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.isDifferentFromNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Completable
import javax.inject.Inject

class PixKeyDeleter @Inject constructor(
  private val bcbClient: BcbClient,
  private val pixKeyRepository: PixKeyRepository
) {

  fun deletePixKey(pixKey: PixKey): Completable {
    return this.deleteOnBcb(pixKey)
      .doOnComplete {
        this.pixKeyRepository.delete(pixKey)
        Completable.complete()
      }
      .doOnError { error: Throwable -> Completable.error(error) }
  }

  private fun deleteOnBcb(pixKey: PixKey): Completable =
    try {
      bcbClient.deletePixKey(
        key = pixKey.key,
        deletePixKeyRequest = DeletePixKeyRequest(
          key = pixKey.key, participant = pixKey.participant
        )
      )
      Completable.complete()
    } catch (httpClientResponseException: HttpClientResponseException) {
      Completable.error(grpcErrorFromHttpStatus(httpClientResponseException.status))
    }

}

private fun grpcErrorFromHttpStatus(
  httpStatus: HttpStatus
): StatusRuntimeException? {
  if (httpStatus.isDifferentFromNotFound()) {
    return Status.UNAVAILABLE
      .withDescription("pix key deletion service unavailable")
      .augmentDescription(
        "can't delete the pix key because the " +
        "Central Bank of Brazil system is returning an unknown response"
      )
      .asRuntimeException()
  }

  return null
}
