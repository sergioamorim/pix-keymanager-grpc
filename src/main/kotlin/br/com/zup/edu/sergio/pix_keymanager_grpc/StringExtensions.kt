package br.com.zup.edu.sergio.pix_keymanager_grpc

fun String.isNotAnUuid(): Boolean =
  !this.matches(
    Regex(
      pattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\$"
    )
  )
