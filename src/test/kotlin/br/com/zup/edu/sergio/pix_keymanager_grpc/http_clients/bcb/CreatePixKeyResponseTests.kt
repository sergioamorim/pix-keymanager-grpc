package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class CreatePixKeyResponseTests {
  @Test
  fun `should return an equivalent PixKey from a CreatePixKeyResponse`() {
    val clientId: String = UUID.randomUUID().toString()
    val key = "10683131000174"
    val participant = "60701190"

    CreatePixKeyResponse(
      keyType = KeyType.CNPJ,
      key = "10683131000174",
      bankAccount = BankAccount(
        participant = participant,
        branch = "0001",
        accountNumber = "123456",
        accountType = AccountType.CACC
      ),
      owner = Owner(
        type = Owner.OwnerType.LEGAL_PERSON,
        name = "Zup",
        taxIdNumber = "10683131000174"
      ),
      createdAt = LocalDateTime.now()
    ).asPixKey(clientId = clientId).also { pixKey: PixKey ->
      assertEquals(PixKey.KeyType.CNPJ, pixKey.type)
      assertEquals(key, pixKey.key)
      assertEquals(clientId, pixKey.clientId)
      assertEquals(PixKey.AccountType.CHECKING, pixKey.accountType)
      assertEquals(participant, pixKey.participant)
    }
  }
}
