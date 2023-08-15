package me.rasztabiga.thesis.delivery.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "courier")
data class CourierEntity(
    @Id
    val id: String,
    val name: String,
    var availability: Availability,
) {
    enum class Availability {
        ONLINE, OFFLINE
    }
}
