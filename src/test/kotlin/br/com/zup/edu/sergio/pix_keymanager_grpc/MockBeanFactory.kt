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
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Completable
import io.reactivex.Single
import java.time.LocalDateTime
import java.util.*

@Factory
class MockBeanFactory {

  val bcbDeleteReturnsNotFoundPixKey: String = "05034262100"
  val bcbDeleteReturnsUnknownResponsePixKey: String = "80756103428"

  val erpReadReturnsNotFoundClientId: String = UUID.randomUUID().toString()
  val erpReadReturnsUnknownStatusClientId: String = UUID.randomUUID().toString()

  @get:Bean
  @get:Replaces(bean = BcbClient::class)
  val bcbClient: BcbClient = object : BcbClient {
    override fun createPixKey(
      createPixKeyRequest: CreatePixKeyRequest
    ): Single<CreatePixKeyResponse> =
      Single.just(
        CreatePixKeyResponse(
          keyType = createPixKeyRequest.keyType,
          key = createPixKeyRequest.key,
          bankAccount = createPixKeyRequest.bankAccount,
          owner = createPixKeyRequest.owner,
          createdAt = LocalDateTime.now()
        )
      )

    override fun deletePixKey(
      key: String, deletePixKeyRequest: DeletePixKeyRequest
    ): Completable =
      when (key) {
        this@MockBeanFactory.bcbDeleteReturnsNotFoundPixKey ->
          Completable.error(
            HttpClientResponseException("", HttpResponse.notFound<Any>())
          )

        this@MockBeanFactory.bcbDeleteReturnsUnknownResponsePixKey -> Completable.error(
          HttpClientResponseException("", HttpResponse.serverError<Any>())
        )

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
        this@MockBeanFactory.erpReadReturnsUnknownStatusClientId -> Single.error(
          HttpClientResponseException("", HttpResponse.serverError<Any>())
        )

        this@MockBeanFactory.erpReadReturnsNotFoundClientId -> Single.error(
          HttpClientResponseException("", HttpResponse.notFound<Any>())
        )

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
