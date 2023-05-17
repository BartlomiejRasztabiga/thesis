package me.rasztabiga.thesis.order.domain.query.query

import java.util.UUID

data class FindOrderByIdQuery(
    val orderId: UUID
)
