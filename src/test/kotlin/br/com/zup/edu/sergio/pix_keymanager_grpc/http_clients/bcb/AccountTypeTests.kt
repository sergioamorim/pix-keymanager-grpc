package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccountTypeTests {
  @Test
  fun `should be able to convert itself when CACC to AccountType from Account from PixKeyReadingOneResponse`() {
    Assertions.assertEquals(
      PixKeyReadingOneResponse.Account.AccountType.CHECKING,
      AccountType.CACC.pixKeyReadingOneResponseAccountType()
    )
  }

  @Test
  fun `should be able to convert itself when SVGS to AccountType from Account from PixKeyReadingOneResponse`() {
    Assertions.assertEquals(
      PixKeyReadingOneResponse.Account.AccountType.SAVINGS,
      AccountType.SVGS.pixKeyReadingOneResponseAccountType()
    )
  }
}
