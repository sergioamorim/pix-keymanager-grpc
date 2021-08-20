package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OwnerTests {
  @Test
  fun `should have type (OwnerType), name and taxIdNumber (String) properties`() {
    val type: Owner.OwnerType = Owner.OwnerType.NATURAL_PERSON
    val name = "Sergio"
    val taxIdNumber = "25934333372"

    Owner(type = type, name = name, taxIdNumber = taxIdNumber)
      .also { owner: Owner ->
        assertEquals(type, owner.type)
        assertEquals(name, owner.name)
        assertEquals(taxIdNumber, owner.taxIdNumber)
      }
  }
}
