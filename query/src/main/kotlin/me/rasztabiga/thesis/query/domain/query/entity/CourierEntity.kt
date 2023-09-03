package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "courier")
data class CourierEntity(
    @Id
    val id: String,
    val name: String,
    val email: String,
    var availability: Availability,
) {
    enum class Availability {
        ONLINE, OFFLINE
    }
}
