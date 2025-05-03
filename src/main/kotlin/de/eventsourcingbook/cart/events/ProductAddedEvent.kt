package de.eventsourcingbook.cart.events

import java.util.UUID

data class ProductAddedEvent(
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
)
