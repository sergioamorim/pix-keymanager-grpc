package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyType
import java.util.*

fun PixKeyCreationRequest.isCpfKey(): Boolean = this.type.equals(PixKeyType.CPF)

fun PixKeyCreationRequest.isPhoneNumberKey(): Boolean =
  this.type.equals(PixKeyType.PHONE_NUMBER)

fun PixKeyCreationRequest.isRandomKey(): Boolean =
  this.type.equals(PixKeyType.RANDOM)

fun PixKeyCreationRequest.isNotRandomKey(): Boolean = !this.isRandomKey()

fun PixKeyCreationRequest.isEmailKey(): Boolean =
  this.type.equals(PixKeyType.EMAIL)

fun PixKeyCreationRequest.hasNotAValidCpfKey(): Boolean =
  !this.key.matches(Regex(pattern = "^[0-9]{11}\$"))

fun PixKeyCreationRequest.hasNotAValidPhoneNumberKey(): Boolean =
  !this.key.matches(Regex(pattern = "^\\+[1-9][0-9]\\d{1,14}\$"))

fun PixKeyCreationRequest.hasNotAValidEmailKey(): Boolean =
  !this.key.matches(Regex(pattern = ".+@.+\\..+"))

fun PixKeyCreationRequest.hasInvalidAccountType(): Boolean =
  this.accountType.equals(AccountType.UNKNOWN_ACCOUNT_TYPE)

fun PixKeyCreationRequest.asPixKey(): PixKey =
  PixKey(
    type = this.type,
    key = this.keyOrNewRandomKey(),
    clientId = this.clientId,
    accountType = this.accountType
  )

fun PixKeyCreationRequest.keyOrNewRandomKey(): String =
  if (PixKeyType.RANDOM == this.type) {
    UUID.randomUUID().toString()
  } else {
    this.key
  }

fun PixKeyCreationRequest.lengthIsGreaterThan(maxLength: Int): Boolean =
  this.key.length > maxLength
