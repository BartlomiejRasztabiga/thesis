package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import java.math.BigDecimal
import java.util.*

data class Product(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val imageUrl: String
)
