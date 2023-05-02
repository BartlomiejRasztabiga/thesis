package me.rasztabiga.thesis.restaurant.domain.command.event

import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Product
import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantMenuUpdatedEvent(
    val id: UUID,
    val menu: List<Product>
)
