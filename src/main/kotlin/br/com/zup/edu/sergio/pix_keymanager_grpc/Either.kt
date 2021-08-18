package br.com.zup.edu.sergio.pix_keymanager_grpc

sealed class Either<out L, out R> {
  class Left<L>(val left: L) : Either<L, Nothing>()
  class Right<R>(val right: R) : Either<Nothing, R>()
}
