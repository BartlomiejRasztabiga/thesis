package me.rasztabiga.thesis.restaurant.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document
data class RestaurantEntity(
    @Id
    val id: UUID,
    val name: String
)
