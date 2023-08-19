package me.rasztabiga.thesis.shared.domain.query.query

import java.util.UUID

data class FindOrderByIdQuery(
    val orderId: UUID
)
