package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import java.util.*

fun PixKeyCreationRequest.isCpfKey(): Boolean =
  this.type.equals(PixKeyCreationRequest.KeyType.CPF)

fun PixKeyCreationRequest.isPhoneNumberKey(): Boolean =
  this.type.equals(PixKeyCreationRequest.KeyType.PHONE_NUMBER)

fun PixKeyCreationRequest.isRandomKey(): Boolean =
  this.type.equals(PixKeyCreationRequest.KeyType.RANDOM)

fun PixKeyCreationRequest.isNotRandomKey(): Boolean = !this.isRandomKey()

fun PixKeyCreationRequest.isEmailKey(): Boolean =
  this.type.equals(PixKeyCreationRequest.KeyType.EMAIL)

fun PixKeyCreationRequest.hasNotAValidCpfKey(): Boolean =
  !this.key.matches(Regex(pattern = "^[0-9]{11}\$"))

fun PixKeyCreationRequest.hasNotAValidPhoneNumberKey(): Boolean =
  !this.key.matches(Regex(pattern = "^\\+[1-9][0-9]\\d{1,14}\$"))

fun PixKeyCreationRequest.hasNotAValidEmailKey(): Boolean =
  !this.key.matches(Regex(pattern = ".+@.+\\..+"))

fun PixKeyCreationRequest.hasInvalidAccountType(): Boolean =
  null == this.accountType.toModel()

fun PixKeyCreationRequest.hasInvalidType(): Boolean =
  null == this.type.toModel()

fun PixKeyCreationRequest.asPixKey(): PixKey {
  assert(null != this.type.toModel()) {
    """
      PixKeyCreationRequest.asPixKey should never be called with an invalid key 
      type (type). A PixKey object can't be created without a valid type."
    """.trimIndent()
  }
  assert(null != this.accountType.toModel()) {
    """
      PixKeyCreationRequest.asPixKey should never be called with an invalid 
      account type (accountType). A PixKey object can't be created without a
      valid accountType."
    """.trimIndent()
  }
  return PixKey(
    type = this.type.toModel()!!,
    key = this.keyOrNewRandomKey(),
    clientId = this.clientId,
    accountType = this.accountType.toModel()!!
  )
}

fun PixKeyCreationRequest.keyOrNewRandomKey(): String =
  if (PixKeyCreationRequest.KeyType.RANDOM == this.type) {
    UUID.randomUUID().toString()
  } else {
    this.key
  }

fun PixKeyCreationRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength

fun PixKeyCreationRequest.KeyType.toModel(): PixKey.KeyType? =
  when (this) {
    PixKeyCreationRequest.KeyType.CPF -> PixKey.KeyType.CPF
    PixKeyCreationRequest.KeyType.EMAIL -> PixKey.KeyType.EMAIL
    PixKeyCreationRequest.KeyType.PHONE_NUMBER -> PixKey.KeyType.PHONE_NUMBER
    PixKeyCreationRequest.KeyType.RANDOM -> PixKey.KeyType.RANDOM
    else -> null
  }

fun PixKeyCreationRequest.AccountType.toModel(): PixKey.AccountType? =
  when (this) {
    PixKeyCreationRequest.AccountType.CHECKING -> PixKey.AccountType.CHECKING
    PixKeyCreationRequest.AccountType.SAVINGS -> PixKey.AccountType.SAVINGS
    else -> null
  }
