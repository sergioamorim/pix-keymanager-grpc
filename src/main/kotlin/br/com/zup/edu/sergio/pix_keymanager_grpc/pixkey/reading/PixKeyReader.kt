package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.StrParticipants
import br.com.zup.edu.sergio.pix_keymanager_grpc.googleProtobufTimestamp
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.PixKeyDetailsResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyTinyDao
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingAllResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Singleton
class PixKeyReader @Inject constructor(
  private val bcbClient: BcbClient,
  private val strParticipants: StrParticipants,
  private val pixKeyRepository: PixKeyRepository
) {

  fun readOnePixKey(
    pixKeyReadingOneRequest: PixKeyReadingOneRequest
  ): Mono<PixKeyReadingOneResponse> {
    if (pixKeyReadingOneRequest.isLocal) {
      return this.bcbReadOnePixKey(
        key = this.pixKeyRepository.getKeyById(id = pixKeyReadingOneRequest.pixId),
        clientId = pixKeyReadingOneRequest.clientId,
        pixId = pixKeyReadingOneRequest.pixId
      )
    }

    return this.bcbReadOnePixKey(key = pixKeyReadingOneRequest.pixKey)
  }

  fun readAllPixKeys(clientId: String): Flux<PixKeyReadingAllResponse.PixKey> {
    return this.pixKeyRepository.findAllByClientId(clientId = clientId)
      .flatMap { pixKeyTinyDao: PixKeyTinyDao ->
        this.bcbClient.readOnePixKey(key = pixKeyTinyDao.key)
          .map { pixKeyDetailsResponse: PixKeyDetailsResponse ->
            PixKeyReadingAllResponse.PixKey
              .newBuilder()
              .setPixId(pixKeyTinyDao.id)
              .setClientId(clientId)
              .setType(pixKeyDetailsResponse.protobufKeyType)
              .setKey(pixKeyDetailsResponse.key)
              .setAccountType(pixKeyDetailsResponse.protobufAccountType)
              .setCreation(pixKeyDetailsResponse.createdAt.googleProtobufTimestamp)
              .build()
          }
          .onErrorResume { Mono.empty() }
          .flux()
      }
  }

  private fun bcbReadOnePixKey(
    key: String, clientId: String = "", pixId: String = ""
  ): Mono<PixKeyReadingOneResponse> {

    return this.bcbClient
      .readOnePixKey(key = key)
      .flatMap { pixKeyDetailsResponse: PixKeyDetailsResponse ->
        Mono.just(
          PixKeyReadingOneResponse
            .newBuilder()
            .setPixId(pixId)
            .setClientId(clientId)
            .setKeyType(pixKeyDetailsResponse.protobufKeyType)
            .setKey(pixKeyDetailsResponse.key)
            .setAccount(
              pixKeyDetailsResponse.asPixKeyReadingOneResponseAccount(
                strParticipants = this.strParticipants
              )
            )
            .build()
        )
      }
      .onErrorMap(::translatedError)
  }
}

private fun translatedError(error: Throwable) =
  when (error) {
    is HttpClientResponseException -> responseError(error)

    is HttpClientException -> Status.UNAVAILABLE
      .withDescription("bcb one pix key reading service unavailable")
      .augmentDescription(
        "unable to create the pix key because the bcb system's " +
        "one pix key reading service isn't responding"
      )
      .asRuntimeException()

    else -> Status.INTERNAL
      .withDescription("internal problem on one pix key reading")
      .augmentDescription(
        "unexpected behavior when connecting to the bcb system's " +
        "one pix key reading service"
      )
      .asRuntimeException()
  }

private fun responseError(error: HttpClientResponseException): Throwable {
  if (error.isNotFound()) {
    return Status.NOT_FOUND
      .withDescription("pix key could not be found within the bcb system")
      .augmentDescription(
        "the pix key was not found by the bcb system's one pix key reading service"
      )
      .asRuntimeException()
  }

  return Status.UNAVAILABLE
    .withDescription("bcb one pix key reading service unavailable")
    .augmentDescription(
      "can't read the pix key because the bcb system's one pix key reading " +
      "service is returning an unknown response"
    )
    .asRuntimeException()
}
