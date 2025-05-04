package de.eventsourcingbook.cart.domain

import de.eventsourcingbook.cart.common.CommandException
import de.eventsourcingbook.cart.domain.commands.additem.AddProductCommand
import de.eventsourcingbook.cart.domain.commands.additem.UpdateStockCommand
import de.eventsourcingbook.cart.events.ProductAddedEvent
import de.eventsourcingbook.cart.events.StockUpdatedEvent
import java.util.UUID
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Aggregate
class ProductAggregate() {

  @AggregateIdentifier
  lateinit var aggregateId: UUID
  var productId: Long = 0
  var brand: String? = null
  var price: Double = 0.0
  var stock: Int = 0

  @CommandHandler
  constructor(command: AddProductCommand) : this() {
    logger.info { "Handling AddProductCommand: $command" }
    this.aggregateId = command.aggregateId
    AggregateLifecycle.apply(
      ProductAddedEvent(
        aggregateId = command.aggregateId,
        productId = command.productId,
        brand = command.brand,
        price = command.price,
        stock = command.stock
      )
    )
    logger.info { "ProductAddedEvent applied with aggregateId: ${command.aggregateId}, productId: ${command.productId}" }
  }

  @CommandHandler
  fun handle(command: AddProductCommand) {
    logger.info { "Handling AddProductCommand: $command" }
    if (command.price <= 0) {
      throw CommandException("Price must be greater than zero")
    }
    AggregateLifecycle.apply(
        ProductAddedEvent(
            aggregateId = command.aggregateId,
            productId = command.productId,
            brand = command.brand,
            price = command.price,
            stock = command.stock))
    logger.info { "ProductAddedEvent applied for productId: ${command.productId}" }
  }

  @CommandHandler
  fun handle(command: UpdateStockCommand) {
    if (command.stockChange == 0) {
      throw CommandException("Stock change cannot be zero")
    }
    AggregateLifecycle.apply(
        StockUpdatedEvent(productId = command.productId, stockChange = command.stockChange))
  }

  @EventSourcingHandler
  fun on(event: ProductAddedEvent) {
    logger.info { "Applying ProductAddedEvent: $event" }
    this.aggregateId = event.aggregateId
    this.productId = event.productId
    this.brand = event.brand
    this.price = event.price
    this.stock = event.stock
    logger.info { "State updated with aggregateId: ${event.aggregateId}, productId: ${event.productId}" }
  }

  @EventSourcingHandler
  fun on(event: StockUpdatedEvent) {
    this.stock += event.stockChange
  }
}
