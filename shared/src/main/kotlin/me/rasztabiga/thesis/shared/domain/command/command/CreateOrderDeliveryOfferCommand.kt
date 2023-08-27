package me.rasztabiga.thesis.shared.domain.command.command

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateOrderDeliveryOfferCommand(
    @TargetAggregateIdentifier val id: UUID,
    val orderId: UUID,
    val restaurantLocation: Location,
    val deliveryLocation: Location
)
