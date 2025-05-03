package de.eventsourcingbook.cart.support.internal

import de.eventsourcingbook.cart.domain.commands.additem.AddProductCommand
import java.io.File
import java.util.UUID
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service

@Service
class CsvDataLoader(private val commandGateway: CommandGateway) {

  fun loadCsvData(filePath: String) {
    val file = File(filePath)
    if (!file.exists() || !file.isFile) {
      throw IllegalArgumentException("File not found: $filePath")
    }

    file.useLines { lines ->
      lines.drop(1).forEach { line ->
        val columns = line.split(",")
        if (columns.size >= 8) {
          val productId = UUID.randomUUID()
          val brand = columns[5].ifBlank { null }
          val price = columns[6].toDoubleOrNull() ?: 0.0
          val stock = 100 // Default stock value for now

          val command =
              AddProductCommand(
                  aggregateId = productId,
                  productId = productId,
                  brand = brand,
                  price = price,
                  stock = stock)
          commandGateway.send<Any>(command)
        }
      }
    }
  }
}
