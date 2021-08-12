package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel

@Factory
class GrpcClientFactory {
  @Bean
  fun newBlockingStub(
    @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
  ): PixKeyServiceGrpc.PixKeyServiceBlockingStub =
    PixKeyServiceGrpc.newBlockingStub(channel)
}
