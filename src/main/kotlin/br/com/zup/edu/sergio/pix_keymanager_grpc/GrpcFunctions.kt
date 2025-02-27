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

fun fieldIsNotAnUuidViolation(field: String): StatusRuntimeException =
  fieldViolation(
    field = field,
    status = io.grpc.Status.INVALID_ARGUMENT,
    description = "must be an UUID (ex.: 123e4567-e89b-12d3-a456-426614174000)"
  )

fun clientDoesNotOwnPixKeyViolation(): StatusRuntimeException =
  fieldViolation(
    field = "pix_id",
    status = io.grpc.Status.PERMISSION_DENIED,
    description = "pix_id must be of a pix key which is owned client"
  )

fun pixIdNotFoundViolation(): StatusRuntimeException =
  fieldViolation(
    field = "pix_id",
    status = io.grpc.Status.NOT_FOUND,
    description = "pix_id must be the id of an existing pix key"
  )
