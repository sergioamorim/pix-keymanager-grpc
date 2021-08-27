package br.com.zup.edu.sergio.pix_keymanager_grpc

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
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
