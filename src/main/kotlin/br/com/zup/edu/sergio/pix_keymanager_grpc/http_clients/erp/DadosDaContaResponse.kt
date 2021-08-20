package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.BankAccount
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.Owner
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.AccountType as BcbAccountType

class DadosDaContaResponse(
  private val tipo: AccountType,
  private val instituicao: InstituicaoResponse,

  @field:NotBlank
  @field:Size(min = 4, max = 4)
  private val agencia: String,

  @field:NotBlank
  @field:Size(min = 6, max = 6)
  private val numero: String,
  private val titular: TitularResponse
) {

  val bcbOwner: Owner
    get() = this.titular.bcbOwner

  private val bcbAccountType: BcbAccountType
    get() = when (this.tipo) {
      AccountType.CONTA_CORRENTE -> BcbAccountType.CACC
      AccountType.CONTA_POUPANCA -> BcbAccountType.SVGS
    }

  val bcbBankAccount: BankAccount = BankAccount(
    participant = this.instituicao.ispb,
    branch = this.agencia,
    accountNumber = this.numero,
    accountType = this.bcbAccountType
  )

  class InstituicaoResponse(private val nome: String, val ispb: String)

  class TitularResponse(
    private val id: String, private val nome: String, private val cpf: String
  ) {
    val bcbOwner: Owner
      get() = Owner(
        type = Owner.OwnerType.NATURAL_PERSON,  // no support for LEGAL_PERSON yet
        name = this.nome,
        taxIdNumber = this.cpf
      )
  }
}
