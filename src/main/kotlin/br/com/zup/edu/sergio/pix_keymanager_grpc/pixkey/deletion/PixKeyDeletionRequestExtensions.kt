package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest

val PixKeyDeletionRequest.isClientIdNotAnUuid: Boolean
  get() = this.clientId.isNotAnUuid

val PixKeyDeletionRequest.isPixIdNotAnUuid: Boolean
  get() = this.pixId.isNotAnUuid
