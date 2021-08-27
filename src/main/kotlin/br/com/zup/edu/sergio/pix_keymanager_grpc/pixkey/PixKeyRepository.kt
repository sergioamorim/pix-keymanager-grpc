package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixKeyRepository : JpaRepository<PixKey, String> {
  fun existsByKey(key: String): Boolean

  fun existsByIdAndClientId(id: String, clientId: String): Boolean

  fun getById(id: String): PixKey

  fun getKeyById(id: String): String
}
