package de.eventsourcingbook.cart.events

import java.util.UUID

data class StockUpdatedEvent(val productId: Long, val stockChange: Int)
