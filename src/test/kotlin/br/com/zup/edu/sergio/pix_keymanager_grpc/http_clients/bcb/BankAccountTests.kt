package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BankAccountTests {
  @Test
  fun `should have participant, branch, accountNumber (String) and accountType (AccountType) properties`() {
    val participant = "60701190"
    val branch = "0001"
    val accountNumber = "123456"
    val accountType: AccountType = AccountType.CACC

    BankAccount(
      participant = participant,
      branch = branch,
      accountNumber = accountNumber,
      accountType = accountType
    ).also { bankAccount: BankAccount ->
      assertEquals(participant, bankAccount.participant)
      assertEquals(branch, bankAccount.branch)
      assertEquals(accountNumber, bankAccount.accountNumber)
      assertEquals(accountType, bankAccount.accountType)
    }
  }
}
