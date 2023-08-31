package me.rasztabiga.thesis.payment.domain.command.aggregate

import java.util.*

data class Product(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: Double,
)
