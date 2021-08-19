package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.reactivex.Single

@Client("\${http-clients.erp.server_url}")
interface ErpClient {

  @Get("/api/v1/clientes/{clientId}/contas")
  fun readAccount(
    @PathVariable
    clientId: String,

    @QueryValue("tipo")
    accountType: AccountType?
  ): Single<DadosDaContaResponse>
}
