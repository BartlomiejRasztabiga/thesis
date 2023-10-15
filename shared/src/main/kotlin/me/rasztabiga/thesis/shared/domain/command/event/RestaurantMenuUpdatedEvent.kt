package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.math.BigDecimal
import java.util.*

@Revision("1.0")
data class RestaurantMenuUpdatedEvent(
    val id: UUID,
    val menu: List<Product>
) {
    data class Product(
        val id: UUID,
        val name: String,
        val description: String?,
        val price: BigDecimal,
        val imageUrl: String
    )

}
