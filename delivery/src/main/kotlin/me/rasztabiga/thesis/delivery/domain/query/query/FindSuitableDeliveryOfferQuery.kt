package me.rasztabiga.thesis.delivery.domain.query.query

data class FindSuitableDeliveryOfferQuery(
    val courierId: String,
    val courierAddress: String
)
