package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.grpc.stub.StreamObserver

fun <T> StreamObserver<T>.completeOnNext(response: T) {
  this.onNext(response)
  this.onCompleted()
}
