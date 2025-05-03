package de.eventsourcingbook.cart.domain.commands.additem

import de.eventsourcingbook.cart.common.Command
import java.util.UUID
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AddProductCommand(
    @TargetAggregateIdentifier override var aggregateId: UUID,
    val productId: UUID,
    val brand: String?,
    val price: Double,
    val stock: Int
) : Command
