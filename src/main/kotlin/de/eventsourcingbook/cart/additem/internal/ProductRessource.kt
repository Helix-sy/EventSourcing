package de.eventsourcingbook.cart.additem.internal

import de.eventsourcingbook.cart.domain.commands.additem.AddProductCommand
import de.eventsourcingbook.cart.domain.commands.additem.UpdateStockCommand
import java.util.UUID
import java.util.concurrent.CompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductRessource(private val commandGateway: CommandGateway) {

  @PostMapping
  fun addProduct(@RequestBody payload: AddProductPayload): CompletableFuture<Any> {
    val command =
        AddProductCommand(
            aggregateId = UUID.randomUUID(),
            productId = payload.productId,
            brand = payload.brand,
            price = payload.price,
            stock = payload.stock)
    return commandGateway.send(command)
  }

  @PutMapping("/{productId}/stock")
  fun updateStock(
      @PathVariable productId: UUID,
      @RequestBody payload: UpdateStockPayload
  ): CompletableFuture<Any> {
    val command =
        UpdateStockCommand(
            aggregateId = productId, productId = productId, stockChange = payload.stockChange)
    return commandGateway.send(command)
  }

  @GetMapping
  fun listProducts(): List<ProductResponse> {
    // Mocked response for now; replace with actual service call
    return listOf(
        ProductResponse(
            productId = UUID.randomUUID(), brand = "ExampleBrand", price = 19.99, stock = 100))
  }

  @GetMapping("/{productId}")
  fun getProduct(@PathVariable productId: UUID): ProductResponse {
    // Mocked response for now; replace with actual service call
    return ProductResponse(
        productId = productId, brand = "ExampleBrand", price = 19.99, stock = 100)
  }
}

data class AddProductPayload(
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
)

data class UpdateStockPayload(val stockChange: Int)

data class ProductResponse(
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
)
