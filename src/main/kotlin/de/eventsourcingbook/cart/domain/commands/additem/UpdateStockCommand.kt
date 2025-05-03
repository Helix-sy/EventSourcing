package de.eventsourcingbook.cart.domain.commands.additem

import de.eventsourcingbook.cart.common.Command
import java.util.UUID
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class UpdateStockCommand(
    @TargetAggregateIdentifier override var aggregateId: UUID,
    val productId: UUID,
    val stockChange: Int
) : Command
