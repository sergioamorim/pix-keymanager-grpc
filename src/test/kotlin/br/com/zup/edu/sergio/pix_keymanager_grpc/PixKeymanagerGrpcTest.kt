package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class PixKeymanagerGrpcTest {

  @Inject
  lateinit var application: EmbeddedApplication<*>

  @Test
  fun testItWorks() {
    Assertions.assertTrue(application.isRunning)
  }

}
