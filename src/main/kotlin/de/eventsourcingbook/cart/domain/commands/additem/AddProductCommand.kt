package de.eventsourcingbook.cart.domain.commands.additem

import de.eventsourcingbook.cart.common.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class AddProductCommand(
    @TargetAggregateIdentifier override var aggregateId: UUID,
    val productId: Long,
    val brand: String?,
    val price: Double,
    val stock: Int
) : Command
