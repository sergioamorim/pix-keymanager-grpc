package br.com.zup.edu.sergio.pix_keymanager_grpc

import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

val LocalDateTime.googleProtobufTimestamp: Timestamp
  get() = Timestamp
    .newBuilder()
    .setSeconds(this.toEpochSecond(ZoneOffset.UTC))
    .setNanos(this.nano)
    .build()
