package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingAllRequest

val PixKeyReadingAllRequest.isClientIdNotAnUuid: Boolean
  get() = this.clientId.isNotAnUuid
