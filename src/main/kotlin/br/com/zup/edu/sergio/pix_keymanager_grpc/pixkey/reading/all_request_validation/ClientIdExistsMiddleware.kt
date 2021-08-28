package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading.all_request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.fieldViolation
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingAllRequest
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import reactor.core.publisher.Mono

class ClientIdExistsMiddleware(
  private val erpClient: ErpClient
) : RequestMiddleware<PixKeyReadingAllRequest>() {
  override fun check(request: PixKeyReadingAllRequest): Mono<PixKeyReadingAllRequest> {
    return this.erpClient.readClient(clientId = request.clientId)
      .flatMap { this.checkNext(request = request) }
      .onErrorMap(::translatedError)
  }
}

fun translatedError(error: Throwable): Throwable =
  when (error) {
    is HttpClientResponseException -> responseError(httpClientResponseException = error)

    is HttpClientException -> Status.UNAVAILABLE
      .withDescription("erp client reading service unavailable")
      .augmentDescription(
        "unable confirm the client_id existence because the erp system's " +
        "client reading service isn't responding"
      )
      .asRuntimeException()

    else -> Status.INTERNAL
      .withDescription("internal problem on confirming the client_id existence")
      .augmentDescription(
        "unexpected behavior when connecting to the erp system's client " +
        "reading service"
      )
      .asRuntimeException()
  }

fun responseError(httpClientResponseException: HttpClientResponseException): Throwable {
  if (httpClientResponseException.isNotFound()) {
    return fieldViolation(
      field = "client_id",
      status = Status.NOT_FOUND,
      description = "must exist within the erp system"
    )
  }

  return Status.UNAVAILABLE
    .withDescription("erp client reading service unavailable")
    .augmentDescription(
      "can't confirm the client_id existence because the erp system's client " +
      "reading service is returning an unknown response"
    )
    .asRuntimeException()
}
