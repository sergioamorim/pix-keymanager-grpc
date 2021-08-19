package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.KeyType
import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotAnUuid
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest

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
  this.accountType !in listOf(
    PixKeyCreationRequest.AccountType.CHECKING,
    PixKeyCreationRequest.AccountType.SAVINGS
  )

fun PixKeyCreationRequest.hasInvalidType(): Boolean =
  this.type !in listOf(
    PixKeyCreationRequest.KeyType.CPF,
    PixKeyCreationRequest.KeyType.EMAIL,
    PixKeyCreationRequest.KeyType.PHONE_NUMBER,
    PixKeyCreationRequest.KeyType.RANDOM
  )

fun PixKeyCreationRequest.isClientIdNotAnUuid(): Boolean =
  this.clientId.isNotAnUuid()

fun PixKeyCreationRequest.bcbKeyType(): KeyType =
  when (this.type) {
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

fun PixKeyCreationRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength

fun PixKeyCreationRequest.erpAccountType(): AccountType =
  when (this.accountType) {
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
