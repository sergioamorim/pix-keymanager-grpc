package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import br.com.zup.edu.sergio.pix_keymanager_grpc.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyType
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class PixKey(
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  val type: PixKeyType,

  @Column(nullable = false)
  val key: String,

  @Column(nullable = false)
  val clientId: String,

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  val accountType: AccountType
) {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  val id: String? = null
}
