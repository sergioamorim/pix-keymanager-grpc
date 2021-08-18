package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

class Owner(val type: OwnerType, val name: String, val taxIdNumber: String) {
  enum class OwnerType { NATURAL_PERSON, LEGAL_PERSON }
}
