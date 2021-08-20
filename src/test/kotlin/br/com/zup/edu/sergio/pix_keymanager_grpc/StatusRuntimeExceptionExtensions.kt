package br.com.zup.edu.sergio.pix_keymanager_grpc

import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import org.junit.jupiter.api.Assertions

fun StatusRuntimeException.assertStatus(status: Status) {
  Assertions.assertEquals(status.code, this.status.code)
}

fun StatusRuntimeException.assertIsFieldViolationWithADescription(field: String) {
  val details: com.google.protobuf.Any? = StatusProto
    .fromThrowable(this)
    ?.getDetails(0)

  Assertions.assertNotNull(details)
  Assertions.assertTrue(details!!.`is`(BadRequest.FieldViolation::class.java))

  val fieldViolation: BadRequest.FieldViolation =
    details.unpack(BadRequest.FieldViolation::class.java)

  Assertions.assertEquals(field, fieldViolation.field)
  Assertions.assertTrue(fieldViolation.description.isNotBlank())
}
