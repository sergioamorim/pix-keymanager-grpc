package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel

@Factory
class GrpcClientFactory {
  @Bean
  fun newPixKeyCreationBlockingStub(
    @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
  ): PixKeyCreationServiceGrpc.PixKeyCreationServiceBlockingStub =
    PixKeyCreationServiceGrpc.newBlockingStub(channel)

  @Bean
  fun newPixKeyDeletionBlockingStub(
    @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
  ): PixKeyDeletionServiceGrpc.PixKeyDeletionServiceBlockingStub =
    PixKeyDeletionServiceGrpc.newBlockingStub(channel)
}
