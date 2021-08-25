package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest

fun PixKeyReadingOneRequest.isLocal(): Boolean =
  this.pixId.isNotBlank() and this.pixKey.isBlank()

fun PixKeyReadingOneRequest.isClientIdNotAnUuid(): Boolean =
  this.clientId.isNotAnUuid()

fun PixKeyReadingOneRequest.isPixIdNotAnUuid(): Boolean =
  this.pixId.isNotAnUuid()
