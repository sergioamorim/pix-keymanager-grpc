package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.http.HttpStatus

fun HttpStatus.isDifferentFromNotFound(): Boolean =
  HttpStatus.NOT_FOUND != this

fun HttpStatus.isUnprocessableEntity(): Boolean =
  HttpStatus.UNPROCESSABLE_ENTITY == this
