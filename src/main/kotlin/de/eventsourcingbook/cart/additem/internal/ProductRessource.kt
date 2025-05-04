package de.eventsourcingbook.cart.additem.internal

import de.eventsourcingbook.cart.domain.commands.additem.AddProductCommand
import de.eventsourcingbook.cart.domain.commands.additem.UpdateStockCommand
import de.eventsourcingbook.cart.events.ProductAddedEvent
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import de.eventsourcingbook.cart.additem.internal.ProductEntity
import de.eventsourcingbook.cart.additem.internal.ProductRepository
import org.springframework.data.jpa.repository.JpaRepository
import jakarta.persistence.*
import mu.KotlinLogging
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
)

interface ProductRepository : JpaRepository<ProductEntity, UUID>

@Component
class ProductProjection(
    private val productRepository: ProductRepository
) {
    @Transactional
    @EventHandler
    fun on(event: ProductAddedEvent) {
        logger.info { "Handling ProductAddedEvent: $event" }
        val entity = ProductEntity(
            productId = event.aggregateId,
            brand = event.brand,
            price = event.price,
            stock = event.stock
        )
        productRepository.save(entity)
        logger.info { "Product saved to database: $entity" }
    }
    fun getAll(): List<ProductResponse> = productRepository.findAll().map {
        ProductResponse(
            productId = it.productId,
            brand = it.brand,
            price = it.price,
            stock = it.stock
        )
    }
    fun getById(id: UUID): ProductResponse? = productRepository.findById(id).orElse(null)?.let {
        ProductResponse(
            productId = it.productId,
            brand = it.brand,
            price = it.price,
            stock = it.stock
        )
    }
}

@RestController
@RequestMapping("/products")
class ProductRessource(
    private val commandGateway: CommandGateway,
    private val productProjection: ProductProjection
) {

  @PostMapping
  fun addProduct(@RequestBody payload: AddProductPayload): ResponseEntity<ProductCreatedResponse> {
    val aggregateId = UUID.randomUUID()
    val command =
        AddProductCommand(
            aggregateId = aggregateId,
            productId = payload.productId,
            brand = payload.brand,
            price = payload.price,
            stock = payload.stock)
    commandGateway.sendAndWait<Unit>(command)
    return ResponseEntity.ok(ProductCreatedResponse(aggregateId))
  }

  @Operation(
    summary = "Update product stock",
    description = "Updates the stock for a product aggregate. Use the aggregateId returned from product creation.",
    requestBody = SwaggerRequestBody(
      required = true,
      content = [
        Content(
          mediaType = "application/json",
          schema = Schema(implementation = UpdateStockPayload::class)
        )
      ]
    ),
    responses = [
      ApiResponse(responseCode = "200", description = "Stock updated successfully."),
      ApiResponse(responseCode = "400", description = "Invalid input or aggregate not found.")
    ]
  )
  @PutMapping("/{aggregateId}/stock")
  fun updateStock(
      @PathVariable aggregateId: UUID,
      @RequestBody payload: UpdateStockPayload
  ): CompletableFuture<Any> {
    val command =
        UpdateStockCommand(
            aggregateId = aggregateId,
            productId = payload.productId,
            stockChange = payload.stockChange)
    return commandGateway.send(command)
  }

  @GetMapping
  fun listProducts(): List<ProductResponse> = productProjection.getAll()

  @GetMapping("/{productId}")
  fun getProduct(@PathVariable productId: UUID): ProductResponse? = productProjection.getById(productId)
}

data class AddProductPayload(
    val productId: Long,
    val brand: String?,
    val price: Double,
    val stock: Int
)

data class UpdateStockPayload(val productId: Long, val stockChange: Int)

data class ProductResponse(
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
)

data class ProductCreatedResponse(val aggregateId: UUID)
