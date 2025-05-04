package de.eventsourcingbook.cart.events

import java.util.UUID

data class ProductAddedEvent(
    val aggregateId: UUID,
    val productId: Long,
    val brand: String?,
    val price: Double,
    val stock: Int
)
