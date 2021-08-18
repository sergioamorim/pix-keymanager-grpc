package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.DadosDaContaResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpAccountReader
import br.com.zup.edu.sergio.pix_keymanager_grpc.isUnprocessableEntity
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixKeyCreator @Inject constructor(
  private val erpAccountReader: ErpAccountReader,
  private val bcbClient: BcbClient,
  private val pixKeyRepository: PixKeyRepository
) {

  fun createPixKey(
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Single<PixKeyCreationResponse> {
    return this.erpAccountReader
      .readAccount(pixKeyCreationRequest = pixKeyCreationRequest)
      .flatMap { dadosDaContaResponse: DadosDaContaResponse ->
        this.createPixKeyWithDadosDaContaResponse(
          pixKeyCreationRequest = pixKeyCreationRequest,
          dadosDaContaResponse = dadosDaContaResponse
        )
      }
  }

  private fun createPixKeyWithDadosDaContaResponse(
    pixKeyCreationRequest: PixKeyCreationRequest,
    dadosDaContaResponse: DadosDaContaResponse
  ): Single<PixKeyCreationResponse> =
    this
      .creationResult(pixKeyCreationRequest, dadosDaContaResponse)
      .flatMap { createPixKeyResponse: CreatePixKeyResponse ->
        this.pixCreationResponseOfSavedPixKey(
          createPixKeyResponse = createPixKeyResponse,
          pixKeyCreationRequest = pixKeyCreationRequest
        )
      }

  private fun pixCreationResponseOfSavedPixKey(
    createPixKeyResponse: CreatePixKeyResponse,
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Single<PixKeyCreationResponse> {
    return Single.just(
      PixKeyCreationResponse
        .newBuilder()
        .setPixId(
          this.pixKeyRepository.save(
            createPixKeyResponse.asPixKey(pixKeyCreationRequest.clientId)
          ).id
        )
        .build()
    )
  }

  private fun creationResult(
    pixKeyCreationRequest: PixKeyCreationRequest,
    dadosDaContaResponse: DadosDaContaResponse
  ): Single<CreatePixKeyResponse> =
    this.bcbClient
      .createPixKey(
        createPixKeyRequest = CreatePixKeyRequest(
          pixKeyCreationRequest = pixKeyCreationRequest,
          dadosDaContaResponse = dadosDaContaResponse
        )
      )
      .doOnError { error: Throwable ->
        if (error is HttpClientResponseException) {
          Single.error<CreatePixKeyResponse>(translatedError(error = error))
        }
      }

}

private fun translatedError(
  error: HttpClientResponseException
): StatusRuntimeException {
  if (error.isUnprocessableEntity()) {
    return Status.ALREADY_EXISTS
      .withDescription("pix key must be unique")
      .augmentDescription(
        "the pix key already exists within the bcb system"
      )
      .asRuntimeException()
  }

  return Status.UNAVAILABLE
    .withDescription("bcb pix key creation service unavailable")
    .augmentDescription(
      "can't create the pix key because the bcb system is " +
      "returning an unknown response"
    )
    .asRuntimeException()
}
