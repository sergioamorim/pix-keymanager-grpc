package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest

val PixKeyReadingOneRequest.isLocal: Boolean
  get() = this.pixId.isNotBlank() && this.clientId.isNotBlank() && this.pixKey.isBlank()

val PixKeyReadingOneRequest.isExternal: Boolean
  get() = this.pixKey.isNotBlank() && this.pixId.isBlank() && this.clientId.isBlank()

val PixKeyReadingOneRequest.isClientIdNotAnUuid: Boolean
  get() = this.clientId.isNotAnUuid

val PixKeyReadingOneRequest.isPixIdNotAnUuid: Boolean
  get() = this.pixId.isNotAnUuid

val PixKeyReadingOneRequest.typeCanNotBeDetermined: Boolean
  get() = !this.isLocal && !this.isExternal
