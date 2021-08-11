package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey

import br.com.zup.edu.sergio.pix_keymanager_grpc.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyType
import java.util.*

fun PixKeyRequest.isCpfKey(): Boolean = this.type.equals(PixKeyType.CPF)

fun PixKeyRequest.isPhoneNumberKey(): Boolean = this.type.equals(PixKeyType.PHONE_NUMBER)

fun PixKeyRequest.isRandomKey(): Boolean = this.type.equals(PixKeyType.RANDOM)

fun PixKeyRequest.isNotRandomKey(): Boolean = !this.isRandomKey()

fun PixKeyRequest.isEmailKey(): Boolean = this.type.equals(PixKeyType.EMAIL)

fun PixKeyRequest.hasNotAValidCpfKey(): Boolean =
  !this.key.matches(Regex(pattern = "^[0-9]{11}\$"))

fun PixKeyRequest.hasNotAValidPhoneNumberKey(): Boolean =
  !this.key.matches(Regex(pattern = "^\\+[1-9][0-9]\\d{1,14}\$"))

fun PixKeyRequest.hasNotAValidEmailKey(): Boolean =
  !this.key.matches(Regex(pattern = ".+@.+\\..+"))

fun PixKeyRequest.hasInvalidAccountType(): Boolean =
  this.accountType.equals(AccountType.UNKNOWN_ACCOUNT_TYPE)

fun PixKeyRequest.asPixKey(): PixKey =
  PixKey(
    type = this.type,
    key = this.keyOrNewRandomKey(),
    clientId = this.clientId,
    accountType = this.accountType
  )

fun PixKeyRequest.keyOrNewRandomKey(): String =
  if (PixKeyType.RANDOM == this.type) {
    UUID.randomUUID().toString()
  } else {
    this.key
  }

fun PixKeyRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength
