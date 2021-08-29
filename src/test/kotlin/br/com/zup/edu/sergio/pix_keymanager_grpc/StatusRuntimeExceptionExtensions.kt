package br.com.zup.edu.sergio.pix_keymanager_grpc

import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import org.junit.jupiter.api.Assertions

fun StatusRuntimeException.assertStatus(status: Status) {
  Assertions.assertEquals(status.code, this.status.code, "status does not match")
}

fun StatusRuntimeException.assertIsFieldViolation(field: String, status: Status) {
  val details: com.google.protobuf.Any? = StatusProto
    .fromThrowable(this)
    ?.getDetails(0)

  Assertions.assertNotNull(details, "not a field violation")
  Assertions.assertTrue(
    details!!.`is`(BadRequest.FieldViolation::class.java), "not a field violation"
  )

  val fieldViolation: BadRequest.FieldViolation =
    details.unpack(BadRequest.FieldViolation::class.java)

  Assertions.assertEquals(field, fieldViolation.field, "field does not match")
  Assertions.assertTrue(
    fieldViolation.description.isNotBlank(), "field violation without a description"
  )

  this.assertStatus(status = status)
}
