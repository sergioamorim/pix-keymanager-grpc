package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingServiceGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class PixKeyReadingOneEndpoint : PixKeyReadingServiceGrpc.PixKeyReadingServiceImplBase() {
  override fun readOnePixKey(
    request: PixKeyReadingOneRequest,
    responseObserver: StreamObserver<PixKeyReadingOneResponse>
  ) {
    super.readOnePixKey(request, responseObserver)
  }
}
