package de.eventsourcingbook.cart.support.internal

import de.eventsourcingbook.cart.domain.commands.additem.AddProductCommand
import java.io.File
import java.util.UUID
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import mu.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate

private val logger = KotlinLogging.logger {}

@Service
class CsvDataLoader(private val commandGateway: CommandGateway, private val jdbcTemplate: JdbcTemplate) {

  fun loadCsvData(filePath: String) {
    val file = File(filePath)
    if (!file.exists() || !file.isFile) {
      throw IllegalArgumentException("File not found: $filePath")
    }

    file.useLines { lines ->
      lines.drop(1).forEach { line ->
        val columns = line.split(",")
        if (columns.size >= 9) {
          val eventTime = columns[0]
          val eventType = columns[1]
          val productId = columns[2].toLongOrNull() ?: throw IllegalArgumentException("Invalid productId format: ${columns[2]}")
          val categoryId = columns[3].toLongOrNull()
          val categoryCode = columns[4].ifBlank { null }
          val brand = columns[5].ifBlank { null }
          val price = columns[6].toDoubleOrNull() ?: 0.0
          val userId = columns[7].toLongOrNull()
          val userSession = columns[8]

          // Insert product if not exists
          val insertProductQuery = """
              INSERT INTO products (product_id, brand, price, category_id, category_code)
              VALUES (?, ?, ?, ?, ?)
              ON CONFLICT (product_id) DO NOTHING
          """
          jdbcTemplate.update(insertProductQuery, productId, brand, price, categoryId, categoryCode)

          // Insert event
          val insertEventQuery = """
              INSERT INTO events (event_time, event_type, product_id, user_id, user_session)
              VALUES (?, ?, ?, ?, ?)
          """
          jdbcTemplate.update(insertEventQuery, eventTime, eventType, productId, userId, userSession)
        }
      }
    }
  }
}

@RestController
class CsvImportController(private val csvDataLoader: CsvDataLoader) {

    @Operation(
        summary = "Import products from a CSV file",
        description = "Upload a CSV file to import product data into the database.",
        requestBody = RequestBody(
            content = [
                Content(
                    mediaType = "multipart/form-data",
                    schema = Schema(implementation = MultipartFile::class)
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "CSV file imported successfully!"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid file format or error during processing."
            )
        ]
    )
    @PostMapping("/import-csv")
    fun importCsv(@RequestParam("file") file: MultipartFile): String {
        logger.info { "Received file: ${file.originalFilename}" }
        val tempFile = File.createTempFile("upload", ".csv")
        try {
            file.transferTo(tempFile)
            logger.info { "File successfully transferred to: ${tempFile.absolutePath}" }
            csvDataLoader.loadCsvData(tempFile.absolutePath)
            logger.info { "CSV data successfully loaded." }
        } catch (ex: Exception) {
            logger.error(ex) { "Error processing file: ${file.originalFilename}" }
            throw ex
        } finally {
            tempFile.delete()
            logger.info { "Temporary file deleted: ${tempFile.absolutePath}" }
        }
        return "CSV file imported successfully!"
    }
}

@RestControllerAdvice
class CsvImportErrorHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Map<String, String> {
        return mapOf("error" to ex.message.orEmpty())
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): Map<String, String> {
        return mapOf("error" to "An unexpected error occurred: ${ex.message}")
    }
}
