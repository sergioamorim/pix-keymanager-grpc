package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb.StrParticipantsClient
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.Scheduler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class StrParticipants @Inject constructor(
  private val strParticipantsClient: StrParticipantsClient,
  private val scheduler: Scheduler
) {

  private val objectReader: ObjectReader =
    CsvMapper().readerFor(Map::class.java).with(CsvSchema.emptySchema().withHeader())

  private var strParticipants: Map<String, String> = mapOf()

  fun nameOf(ispb: String): String =
    this.strParticipants[ispb] ?: "unidentified institution"

  @Scheduled(fixedDelay = "24h")
  fun updateStrParticipants() {
    this.strParticipantsClient
      .getStrParticipantsCsv()
      .observeOn(this.scheduler)
      .subscribe(
        { strParticipantsCsvByteArray: ByteArray ->
          this.strParticipants = this.objectReader
            .readValues<Map<String, String>>(strParticipantsCsvByteArray)
            .readAll()
            .associate { row: Map<String, String> ->
              (row["ISPB"] ?: UUID.randomUUID()
                .toString()) to (row["Nome_Extenso"]?.trim() ?: "")
            }
        },
        {}
      )
  }
}
