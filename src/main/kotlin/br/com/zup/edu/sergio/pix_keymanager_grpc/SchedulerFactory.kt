package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

@Factory
class SchedulerFactory {
  @Bean
  fun scheduler(): Scheduler =
    Schedulers.fromExecutor(
      Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
      )
    )
}
