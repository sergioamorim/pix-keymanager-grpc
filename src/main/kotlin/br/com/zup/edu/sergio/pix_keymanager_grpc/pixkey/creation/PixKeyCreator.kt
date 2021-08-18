package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.Either
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
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
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
  ): Either<StatusRuntimeException, PixKeyCreationResponse> =
    when (
      val accountResult: Either<StatusRuntimeException, DadosDaContaResponse> =
        this.erpAccountReader.readAccount(pixKeyCreationRequest)
    ) {
      is Either.Left<StatusRuntimeException> -> Either.Left(accountResult.left)

      is Either.Right<DadosDaContaResponse> -> {
        this.createPixKeyWithDadosDaContaResponse(
          pixKeyCreationRequest, dadosDaContaResponse = accountResult.right
        )
      }
    }

  private fun createPixKeyWithDadosDaContaResponse(
    pixKeyCreationRequest: PixKeyCreationRequest,
    dadosDaContaResponse: DadosDaContaResponse
  ): Either<StatusRuntimeException, PixKeyCreationResponse> =
    when (
      val creationResult: Either<HttpStatus, CreatePixKeyResponse> =
        this.creationResult(pixKeyCreationRequest, dadosDaContaResponse)
    ) {
      is Either.Left -> Either.Left(grpcErrorFromHttpStatus(creationResult.left))

      is Either.Right -> Either.Right(
        this.pixCreationResponseOfSavedPixKey(
          creationResult.right, pixKeyCreationRequest
        )
      )
    }

  private fun pixCreationResponseOfSavedPixKey(
    createPixKeyResponse: CreatePixKeyResponse,
    pixKeyCreationRequest: PixKeyCreationRequest
  ) =
    PixKeyCreationResponse
      .newBuilder()
      .setPixId(
        this.pixKeyRepository.save(
          createPixKeyResponse.asPixKey(pixKeyCreationRequest.clientId)
        ).id
      )
      .build()

  private fun creationResult(
    pixKeyCreationRequest: PixKeyCreationRequest,
    dadosDaContaResponse: DadosDaContaResponse
  ): Either<HttpStatus, CreatePixKeyResponse> =
    try {
      Either.Right(
        this.bcbClient.createPixKey(
          createPixKeyRequest = CreatePixKeyRequest(
            pixKeyCreationRequest = pixKeyCreationRequest,
            dadosDaContaResponse = dadosDaContaResponse
          )
        ).blockingGet()
      )
    } catch (httpClientResponseException: HttpClientResponseException) {
      Either.Left(httpClientResponseException.status)
    }
}

private fun grpcErrorFromHttpStatus(
  httpStatus: HttpStatus
): StatusRuntimeException {
  if (httpStatus.isUnprocessableEntity()) {
    return Status.ALREADY_EXISTS
      .withDescription("pix key must be unique")
      .augmentDescription(
        "the pix key already exists within the Central Bank of Brazil system"
      )
      .asRuntimeException()
  }

  return Status.UNAVAILABLE
    .withDescription("pix key creation service unavailable")
    .augmentDescription(
      "can't create the pix key because the Central Bank of Brazil system " +
      "is returning an unknown response"
    )
    .asRuntimeException()
}
