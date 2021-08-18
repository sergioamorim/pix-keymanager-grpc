package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException

fun HttpClientResponseException.isDifferentFromNotFound(): Boolean =
  !this.isNotFound()

fun HttpClientResponseException.isNotFound(): Boolean =
  HttpStatus.NOT_FOUND == this.status

fun HttpClientResponseException.isUnprocessableEntity(): Boolean =
  HttpStatus.UNPROCESSABLE_ENTITY == this.status
