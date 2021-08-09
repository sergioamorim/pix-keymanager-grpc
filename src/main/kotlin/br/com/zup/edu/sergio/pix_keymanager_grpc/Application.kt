package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
  build()
    .args(*args)
    .packages("br.com.zup.edu.sergio.pix_keymanager_grpc")
    .start()
}
