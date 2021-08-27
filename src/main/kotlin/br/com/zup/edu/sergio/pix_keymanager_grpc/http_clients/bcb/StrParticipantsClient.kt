package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client("\${http-clients.str-participants-csv-url}")
interface StrParticipantsClient {
  @Get(consumes = [MediaType.APPLICATION_OCTET_STREAM])
  fun getStrParticipantsCsv(): Mono<ByteArray>
}
