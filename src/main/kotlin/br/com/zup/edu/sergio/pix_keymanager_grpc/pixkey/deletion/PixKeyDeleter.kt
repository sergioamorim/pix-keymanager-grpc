package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.DeletePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.isDifferentFromNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import com.google.protobuf.Empty
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import reactor.core.publisher.Mono

class PixKeyDeleter @Inject constructor(
  private val bcbClient: BcbClient,
  private val pixKeyRepository: PixKeyRepository
) {

  fun deletePixKey(pixKey: PixKey): Mono<Empty> =
    this.deleteOnBcb(pixKey)
      .doOnSuccess { this.pixKeyRepository.delete(pixKey) }

  private fun deleteOnBcb(pixKey: PixKey): Mono<Empty> =
    this.bcbClient
      .deletePixKey(
        key = pixKey.key,
        deletePixKeyRequest = DeletePixKeyRequest(
          key = pixKey.key, participant = pixKey.participant
        )
      )
      .flatMap { Mono.just(Empty.getDefaultInstance()) }
      .onErrorResume { error: Throwable ->
        translatedError(error = error)?.let { Mono.error(it) }
          ?: Mono.just(Empty.getDefaultInstance())
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
