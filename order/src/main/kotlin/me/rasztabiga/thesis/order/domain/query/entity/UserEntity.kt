package me.rasztabiga.thesis.order.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
data class UserEntity(
    @Id
    val id: String,
    val name: String
)
