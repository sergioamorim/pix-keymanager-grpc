package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.KeyType
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

val PixKeyCreationRequest.isCpfKey: Boolean
  get() = this.type.equals(ProtobufKeyType.KEY_TYPE_CPF)

val PixKeyCreationRequest.isPhoneNumberKey: Boolean
  get() = this.type.equals(ProtobufKeyType.KEY_TYPE_PHONE)

val PixKeyCreationRequest.isRandomKey: Boolean
  get() = this.type.equals(ProtobufKeyType.KEY_TYPE_RANDOM)

val PixKeyCreationRequest.isNotRandomKey: Boolean
  get() = !this.isRandomKey

val PixKeyCreationRequest.isEmailKey: Boolean
  get() = this.type.equals(ProtobufKeyType.KEY_TYPE_EMAIL)

val PixKeyCreationRequest.hasNotAValidCpfKey: Boolean
  get() = !this.key.matches(Regex(pattern = "^[0-9]{11}\$"))

val PixKeyCreationRequest.hasNotAValidPhoneNumberKey: Boolean
  get() = !this.key.matches(Regex(pattern = "^\\+[1-9][0-9]\\d{1,14}\$"))

val PixKeyCreationRequest.hasNotAValidEmailKey: Boolean
  get() = !this.key.matches(Regex(pattern = ".+@.+\\..+"))

val PixKeyCreationRequest.hasInvalidAccountType: Boolean
  get() = this.accountType !in listOf(
    ProtobufAccountType.ACCOUNT_TYPE_CHECKING, ProtobufAccountType.ACCOUNT_TYPE_SAVINGS
  )

val PixKeyCreationRequest.hasInvalidType: Boolean
  get() = this.type !in listOf(
    ProtobufKeyType.KEY_TYPE_CPF,
    ProtobufKeyType.KEY_TYPE_EMAIL,
    ProtobufKeyType.KEY_TYPE_PHONE,
    ProtobufKeyType.KEY_TYPE_RANDOM
  )

val PixKeyCreationRequest.isClientIdNotAnUuid: Boolean
  get() = this.clientId.isNotAnUuid

fun PixKeyCreationRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength

val PixKeyCreationRequest.bcbKeyType: KeyType
  get() = when (this.type) {
    ProtobufKeyType.KEY_TYPE_CPF -> KeyType.CPF
    ProtobufKeyType.KEY_TYPE_PHONE -> KeyType.PHONE
    ProtobufKeyType.KEY_TYPE_EMAIL -> KeyType.EMAIL
    ProtobufKeyType.KEY_TYPE_RANDOM -> KeyType.RANDOM
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
    ProtobufAccountType.ACCOUNT_TYPE_CHECKING -> AccountType.CONTA_CORRENTE
    ProtobufAccountType.ACCOUNT_TYPE_SAVINGS -> AccountType.CONTA_POUPANCA
    else -> throw AssertionError(
      """
        PixKeyCreationRequest.erpAccountType should never be called with an 
        invalid account type (accountType). Can't convert 
        PixKeyCreationRequest.AccountType to bcb.AccountType without a valid
        account type.
      """.trimIndent()
    )
  }
