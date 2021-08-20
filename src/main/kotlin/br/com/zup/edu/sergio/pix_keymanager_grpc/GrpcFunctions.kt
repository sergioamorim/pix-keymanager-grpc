package br.com.zup.edu.sergio.pix_keymanager_grpc

import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

fun fieldViolation(
  field: String, status: io.grpc.Status, description: String = ""
): StatusRuntimeException =
  StatusProto.toStatusRuntimeException(
    Status
      .newBuilder()
      .setCode(status.code.value())
      .addDetails(
        Any.pack(
          BadRequest.FieldViolation
            .newBuilder()
            .setField(field)
            .setDescription(description)
            .build()
        )
      )
      .build()
  )
