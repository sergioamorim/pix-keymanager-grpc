package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

@Factory
class SchedulerFactory {
  @Bean
  fun scheduler(): Scheduler =
    Schedulers.from(
      Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
      )
    )
}
