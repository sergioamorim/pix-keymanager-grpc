package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import io.micronaut.core.annotation.Introspected

@Introspected
class PixKeyTinyDao(val id: String, val key: String)
