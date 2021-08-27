package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.erpAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Mono

@Singleton
class ErpAccountReader @Inject constructor(private val erpClient: ErpClient) {

  fun readAccount(
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Mono<DadosDaContaResponse> =
    this.erpClient
      .readAccount(
        clientId = pixKeyCreationRequest.clientId,
        accountType = pixKeyCreationRequest.erpAccountType
      )
      .onErrorMap(::translatedError)
}

private fun translatedError(error: Throwable) =
  when (error) {
    is HttpClientResponseException -> responseError(error)

    is HttpClientException -> Status.UNAVAILABLE
      .withDescription("erp's account confirmation service unavailable")
      .augmentDescription(
        "unable to confirm the account because the erp system's " +
        "account confirmation service isn't responding"
      )
      .asRuntimeException()

    else -> Status.INTERNAL
      .withDescription("internal problem on account confirmation")
      .augmentDescription(
        "unexpected behavior when connecting to the erp system's " +
        "account confirmation service"
      )
      .asRuntimeException()
  }

private fun responseError(
  error: HttpClientResponseException
): Throwable {
  if (error.isNotFound()) {
    return Status.NOT_FOUND
      .withDescription("account not found")
      .augmentDescription(
        "can't confirm that an account exists within the erp " +
        "system with the client id and account type informed"
      )
      .asRuntimeException()
  }

  return Status.UNAVAILABLE
    .withDescription("account confirmation service unavailable")
    .augmentDescription(
      "can't confirm the account existence because the erp " +
      "system's account confirmation service is returning an unknown response"
    )
    .asRuntimeException()
}
