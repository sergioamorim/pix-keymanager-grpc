package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest

fun PixKeyDeletionRequest.isClientIdNotAnUuid(): Boolean =
  this.clientId.isNotAnUuid()

fun PixKeyDeletionRequest.isPixIdNotAnUuid(): Boolean =
  this.pixId.isNotAnUuid()
