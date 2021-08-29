package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client("\${http-clients.bcb.server-url}")
interface BcbClient {

  @Post(uri = "/api/v1/pix/keys", processes = [MediaType.APPLICATION_XML])
  fun createPixKey(
    @Body createPixKeyRequest: CreatePixKeyRequest
  ): Mono<CreatePixKeyResponse>

  @Get(uri = "/api/v1/pix/keys/{key}", processes = [MediaType.APPLICATION_XML])
  fun readPixKey(@PathVariable key: String): Mono<PixKeyDetailsResponse>

  @Delete(uri = "/api/v1/pix/keys/{key}", processes = [MediaType.APPLICATION_XML])
  fun deletePixKey(
    @PathVariable key: String, @Body deletePixKeyRequest: DeletePixKeyRequest
  ): Mono<String>
}
