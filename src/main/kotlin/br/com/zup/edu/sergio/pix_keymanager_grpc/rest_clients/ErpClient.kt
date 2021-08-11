package br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${http-clients.erp.server_url}")
interface ErpClient {

  @Get("/api/v1/clientes/{clientId}/contas")
  fun readAccount(
    @PathVariable clientId: String,
    @QueryValue("tipo") accountType: ExternalAccountType?
  ): Map<String, Any>

}
