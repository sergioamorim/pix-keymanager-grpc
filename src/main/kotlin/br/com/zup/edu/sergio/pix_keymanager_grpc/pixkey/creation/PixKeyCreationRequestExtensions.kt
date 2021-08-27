package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.KeyType
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest

val PixKeyCreationRequest.isCpfKey: Boolean
  get() = this.type.equals(PixKeyCreationRequest.KeyType.CPF)

val PixKeyCreationRequest.isPhoneNumberKey: Boolean
  get() = this.type.equals(PixKeyCreationRequest.KeyType.PHONE_NUMBER)

val PixKeyCreationRequest.isRandomKey: Boolean
  get() = this.type.equals(PixKeyCreationRequest.KeyType.RANDOM)

val PixKeyCreationRequest.isNotRandomKey: Boolean
  get() = !this.isRandomKey

val PixKeyCreationRequest.isEmailKey: Boolean
  get() = this.type.equals(PixKeyCreationRequest.KeyType.EMAIL)

val PixKeyCreationRequest.hasNotAValidCpfKey: Boolean
  get() = !this.key.matches(Regex(pattern = "^[0-9]{11}\$"))

val PixKeyCreationRequest.hasNotAValidPhoneNumberKey: Boolean
  get() = !this.key.matches(Regex(pattern = "^\\+[1-9][0-9]\\d{1,14}\$"))

val PixKeyCreationRequest.hasNotAValidEmailKey: Boolean
  get() = !this.key.matches(Regex(pattern = ".+@.+\\..+"))

val PixKeyCreationRequest.hasInvalidAccountType: Boolean
  get() = this.accountType !in listOf(
    PixKeyCreationRequest.AccountType.CHECKING,
    PixKeyCreationRequest.AccountType.SAVINGS
  )

val PixKeyCreationRequest.hasInvalidType: Boolean
  get() = this.type !in listOf(
    PixKeyCreationRequest.KeyType.CPF,
    PixKeyCreationRequest.KeyType.EMAIL,
    PixKeyCreationRequest.KeyType.PHONE_NUMBER,
    PixKeyCreationRequest.KeyType.RANDOM
  )

val PixKeyCreationRequest.isClientIdNotAnUuid: Boolean
  get() = this.clientId.isNotAnUuid

fun PixKeyCreationRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength

val PixKeyCreationRequest.bcbKeyType: KeyType
  get() = when (this.type) {
    PixKeyCreationRequest.KeyType.CPF -> KeyType.CPF
    PixKeyCreationRequest.KeyType.PHONE_NUMBER -> KeyType.PHONE
    PixKeyCreationRequest.KeyType.EMAIL -> KeyType.EMAIL
    PixKeyCreationRequest.KeyType.RANDOM -> KeyType.RANDOM
    else -> throw AssertionError(
      """
        PixKeyCreationRequest.bcbKeyType should never be called with an invalid key 
        type (type). Can't convert PixKeyCreationRequest.KeyType to bcb.KeyType without
        a valid type.
      """.trimIndent()
    )
  }

val PixKeyCreationRequest.erpAccountType: AccountType
  get() = when (this.accountType) {
    PixKeyCreationRequest.AccountType.CHECKING -> AccountType.CONTA_CORRENTE
    PixKeyCreationRequest.AccountType.SAVINGS -> AccountType.CONTA_POUPANCA
    else -> throw AssertionError(
      """
        PixKeyCreationRequest.erpAccountType should never be called with an 
        invalid account type (accountType). Can't convert 
        PixKeyCreationRequest.AccountType to bcb.AccountType without a valid
        account type.
      """.trimIndent()
    )
  }
