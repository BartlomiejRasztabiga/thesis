package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateOrderDeliveryOfferCommand(
    @TargetAggregateIdentifier val id: UUID,
    val orderId: UUID,
    val restaurantAddress: String,
    val deliveryAddress: String
)
