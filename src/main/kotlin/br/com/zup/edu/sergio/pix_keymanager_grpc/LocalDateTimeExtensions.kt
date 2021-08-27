package br.com.zup.edu.sergio.pix_keymanager_grpc

import com.google.protobuf.Timestamp
import java.time.LocalDateTime

fun LocalDateTime.asGoogleProtobufTimestamp(): Timestamp =
  Timestamp.newBuilder().setNanos(this.nano).build()
