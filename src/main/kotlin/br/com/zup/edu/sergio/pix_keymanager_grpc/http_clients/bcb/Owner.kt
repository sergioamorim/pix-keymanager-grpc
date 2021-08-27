package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse.Account.Client

class Owner(val type: OwnerType, val name: String, val taxIdNumber: String) {
  enum class OwnerType { NATURAL_PERSON, LEGAL_PERSON }

  fun pixKeyReadingOneResponseAccountClient(): Client =
    Client.newBuilder()
      .setName(this.name)
      .setNationalRegistryId(this.taxIdNumber)
      .build()
}
