package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixKeyRepository : JpaRepository<PixKey, String> {
  fun existsByKey(key: String): Boolean
}
