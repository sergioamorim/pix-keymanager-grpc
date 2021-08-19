package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BcbClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.CreatePixKeyResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.DeletePixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.DadosDaContaResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.ErpClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Completable
import io.reactivex.Single
import java.time.LocalDateTime
import java.util.*

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
    ): Single<CreatePixKeyResponse> =
      when (createPixKeyRequest.key) {
        this@MockBeanFactory.bcbCreateReturnsUnprocessableEntityPixKey ->
          Single.error(
            HttpClientResponseException(
              "", HttpResponse.unprocessableEntity<Any>()
            )
          )

        this@MockBeanFactory.bcbCreateReturnsUnknownResponsePixKey ->
          Single.error(
            HttpClientResponseException("", HttpResponse.serverError<Any>())
          )

        this@MockBeanFactory.bcbCreateReturnsHttpClientExceptionPixKey ->
          Single.error(HttpClientException(""))

        this@MockBeanFactory.bcbCreateReturnsUnknownExceptionPixKey ->
          Single.error(RuntimeException())

        else -> Single.just(
          CreatePixKeyResponse(
            keyType = createPixKeyRequest.keyType,
            key = createPixKeyRequest.key,
            bankAccount = createPixKeyRequest.bankAccount,
            owner = createPixKeyRequest.owner,
            createdAt = LocalDateTime.now()
          )
        )
      }

    override fun deletePixKey(
      key: String, deletePixKeyRequest: DeletePixKeyRequest
    ): Completable =
      when (key) {
        this@MockBeanFactory.bcbDeleteReturnsNotFoundPixKey ->
          Completable.error(
            HttpClientResponseException("", HttpResponse.notFound<Any>())
          )

        this@MockBeanFactory.bcbDeleteReturnsUnknownResponsePixKey ->
          Completable.error(
            HttpClientResponseException("", HttpResponse.serverError<Any>())
          )

        this@MockBeanFactory.bcbDeleteReturnsHttpClientExceptionPixKey ->
          Completable.error(HttpClientException(""))

        this@MockBeanFactory.bcbDeleteReturnsUnknownExceptionPixKey ->
          Completable.error(RuntimeException())

        else -> Completable.complete()
      }
  }

  @get:Bean
  @get:Replaces(ErpClient::class)
  val erpClientMock: ErpClient = object : ErpClient {
    override fun readAccount(
      clientId: String, accountType: AccountType?
    ): Single<DadosDaContaResponse> =
      when (clientId) {
        this@MockBeanFactory.erpReadReturnsUnknownResponseClientId -> Single.error(
          HttpClientResponseException("", HttpResponse.serverError<Any>())
        )

        this@MockBeanFactory.erpReadReturnsNotFoundClientId -> Single.error(
          HttpClientResponseException("", HttpResponse.notFound<Any>())
        )

        this@MockBeanFactory.erpReadReturnsHttpClientExceptionClientId ->
          Single.error(HttpClientException(""))

        this@MockBeanFactory.erpReadReturnsUnknownExceptionClientId ->
          Single.error(RuntimeException())

        else -> Single.just(
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
