package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType

class AccountTypeTests {
  @Test
  fun `should be able to convert itself when CACC to AccountType from Account from PixKeyReadingOneResponse`() {
    Assertions.assertEquals(
      ProtobufAccountType.ACCOUNT_TYPE_CHECKING, AccountType.CACC.protobufAccountType()
    )
  }

  @Test
  fun `should be able to convert itself when SVGS to AccountType from Account from PixKeyReadingOneResponse`() {
    Assertions.assertEquals(
      ProtobufAccountType.ACCOUNT_TYPE_SAVINGS, AccountType.SVGS.protobufAccountType()
    )
  }
}
