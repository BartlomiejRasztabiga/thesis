package me.rasztabiga.thesis.shared.domain.query.query

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

data class FindBestCourierForDeliveryQuery(
    val courierId: String,
    val restaurantLocation: Location
)
