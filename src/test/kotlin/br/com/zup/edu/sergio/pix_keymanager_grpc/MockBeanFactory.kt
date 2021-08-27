package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.DadosDaContaResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.AccountType as BcbAccountType

@Factory
class MockBeanFactory {

  val bcbCreateReturnsUnprocessableEntityPixKey: String = "+55934077699"
  val bcbCreateReturnsUnknownResponsePixKey: String = "+55985124398"
  val bcbCreateReturnsHttpClientExceptionPixKey: String = "+55917894533"
  val bcbCreateReturnsUnknownExceptionPixKey: String = "+55945348090"

  val bcbDeleteReturnsNotFoundPixKey: String = "05034262100"
  val bcbDeleteReturnsUnknownResponsePixKey: String = "80756103428"
  val bcbDeleteReturnsHttpClientExceptionPixKey: String = "25198181900"
  val bcbDeleteReturnsUnknownExceptionPixKey: String = "70529885263"

  val erpReadReturnsNotFoundClientId: String = UUID.randomUUID().toString()
  val erpReadReturnsUnknownResponseClientId: String = UUID.randomUUID().toString()
  val erpReadReturnsHttpClientExceptionClientId: String = UUID.randomUUID().toString()
  val erpReadReturnsUnknownExceptionClientId: String = UUID.randomUUID().toString()

  @get:Bean
  @get:Replaces(bean = BcbClient::class)
  val bcbClient: BcbClient = object : BcbClient {
    override fun createPixKey(
      createPixKeyRequest: CreatePixKeyRequest
    ): Mono<CreatePixKeyResponse> =
      when (createPixKeyRequest.key) {
        this@MockBeanFactory.bcbCreateReturnsUnprocessableEntityPixKey ->
          Mono.error(
            HttpClientResponseException(
              "", HttpResponse.unprocessableEntity<Any>()
            )
          )

        this@MockBeanFactory.bcbCreateReturnsUnknownResponsePixKey ->
          Mono.error(
            HttpClientResponseException("", HttpResponse.serverError<Any>())
          )

        this@MockBeanFactory.bcbCreateReturnsHttpClientExceptionPixKey ->
          Mono.error(HttpClientException(""))

        this@MockBeanFactory.bcbCreateReturnsUnknownExceptionPixKey ->
          Mono.error(RuntimeException())

        else -> Mono.just(
          CreatePixKeyResponse(
            keyType = createPixKeyRequest.keyType,
            key = createPixKeyRequest.key,
            bankAccount = createPixKeyRequest.bankAccount,
            owner = createPixKeyRequest.owner,
            createdAt = LocalDateTime.now()
          )
        )
      }

    override fun readOnePixKey(key: String): Mono<PixKeyDetailsResponse> {
      return Mono.just(
        PixKeyDetailsResponse(
          keyType = KeyType.RANDOM,
          key = key,
          bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "123456",
            accountType = BcbAccountType.CACC
          ),
          owner = Owner(
            type = Owner.OwnerType.NATURAL_PERSON,
            name = "owner name",
            taxIdNumber = "12345678901"
          ),
          createdAt = LocalDateTime.now()
        )
      )
    }

    override fun deletePixKey(
      key: String, deletePixKeyRequest: DeletePixKeyRequest
    ): Mono<String> =
      when (key) {
        this@MockBeanFactory.bcbDeleteReturnsNotFoundPixKey ->
          Mono.error(
            HttpClientResponseException("", HttpResponse.notFound<Any>())
          )

        this@MockBeanFactory.bcbDeleteReturnsUnknownResponsePixKey ->
          Mono.error(
            HttpClientResponseException("", HttpResponse.serverError<Any>())
          )

        this@MockBeanFactory.bcbDeleteReturnsHttpClientExceptionPixKey ->
          Mono.error(HttpClientException(""))

        this@MockBeanFactory.bcbDeleteReturnsUnknownExceptionPixKey ->
          Mono.error(RuntimeException())

        else -> Mono.just("")
      }
  }

  @get:Bean
  @get:Replaces(ErpClient::class)
  val erpClientMock: ErpClient = object : ErpClient {
    override fun readAccount(
      clientId: String, accountType: AccountType?
    ): Mono<DadosDaContaResponse> =
      when (clientId) {
        this@MockBeanFactory.erpReadReturnsUnknownResponseClientId -> Mono.error(
          HttpClientResponseException("", HttpResponse.serverError<Any>())
        )

        this@MockBeanFactory.erpReadReturnsNotFoundClientId -> Mono.error(
          HttpClientResponseException("", HttpResponse.notFound<Any>())
        )

        this@MockBeanFactory.erpReadReturnsHttpClientExceptionClientId ->
          Mono.error(HttpClientException(""))

        this@MockBeanFactory.erpReadReturnsUnknownExceptionClientId ->
          Mono.error(RuntimeException())

        else -> Mono.just(
          DadosDaContaResponse(
            tipo = accountType ?: AccountType.CONTA_CORRENTE,
            instituicao = DadosDaContaResponse.InstituicaoResponse(
              nome = "institution name", ispb = "60701190"
            ),
            agencia = "0001",
            numero = "291900",
            titular = DadosDaContaResponse.TitularResponse(
              id = clientId,
              nome = "client name",
              cpf = "12345678901"
            )
          )
        )
      }
  }
}
