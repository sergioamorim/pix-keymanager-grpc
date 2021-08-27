package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.DadosDaContaResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpAccountReader
import br.com.zup.edu.sergio.pix_keymanager_grpc.isUnprocessableEntity
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Single
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hibernate.exception.ConstraintViolationException

@Singleton
class PixKeyCreator @Inject constructor(
  private val erpAccountReader: ErpAccountReader,
  private val bcbClient: BcbClient,
  private val pixKeyRepository: PixKeyRepository
) {

  fun createPixKey(
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Single<PixKeyCreationResponse> =
    this.erpAccountReader
      .readAccount(pixKeyCreationRequest = pixKeyCreationRequest)
      .flatMap { dadosDaContaResponse: DadosDaContaResponse ->
        this.createPixKeyWithDadosDaContaResponse(
          pixKeyCreationRequest = pixKeyCreationRequest,
          dadosDaContaResponse = dadosDaContaResponse
        )
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
      .onErrorResumeNext { error: Throwable ->
        Single.error(translatedError(error = error))
      }

  private fun pixCreationResponseOfSavedPixKey(
    createPixKeyResponse: CreatePixKeyResponse,
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Single<PixKeyCreationResponse> =
    Single.just(
      PixKeyCreationResponse
        .newBuilder()
        .setPixId(
          this.savedPixKey(
            createPixKeyResponse.asPixKey(pixKeyCreationRequest.clientId)
          ).id
        )
        .build()
    )

  private fun savedPixKey(pixKey: PixKey) =
    try {
      this.pixKeyRepository.save(pixKey)
    } catch (error: Throwable) {
      throw persistenceError(error = error)
    }
}

private fun translatedError(error: Throwable) =
  when (error) {
    is HttpClientResponseException -> responseError(error)

    is HttpClientException -> Status.UNAVAILABLE
      .withDescription("bcb pix key creation service unavailable")
      .augmentDescription(
        "unable to create the pix key because the bcb system's " +
        "pix key creation service isn't responding"
      )
      .asRuntimeException()

    else -> Status.INTERNAL
      .withDescription("internal problem on pix key creation")
      .augmentDescription(
        "unexpected behavior when connecting to the bcb system's " +
        "pix key creation service"
      )
      .asRuntimeException()
  }

private fun responseError(
  error: HttpClientResponseException
): Throwable {
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

private fun persistenceError(error: Throwable): Throwable {
  if (error.cause is ConstraintViolationException) {
    return Status.ABORTED
      .withDescription(
        "can't create a pix key due to a concurrency error"
      )
      .augmentDescription(
        "the pix key could not be persisted after validation, " +
        "most likely due to a concurrency between requests, and the error " +
        "was catch by a constraint on the database"
      )
      .asRuntimeException()
  }

  return Status.INTERNAL
    .withDescription("internal problem on pix key creation")
    .augmentDescription(
      "unexpected behavior when persisting the pix key " +
      "to the database"
    )
    .asRuntimeException()
}
