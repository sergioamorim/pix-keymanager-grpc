package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class PixKey(
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  val type: KeyType,

  @Column(nullable = false)
  val key: String,

  @Column(nullable = false)
  val clientId: String,

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  val accountType: AccountType,

  @Column(nullable = false)
  val participant: String
) {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  val id: String? = null

  enum class KeyType { CPF, PHONE_NUMBER, EMAIL, RANDOM, CNPJ }

  enum class AccountType {
    CHECKING, SAVINGS
  }
}
