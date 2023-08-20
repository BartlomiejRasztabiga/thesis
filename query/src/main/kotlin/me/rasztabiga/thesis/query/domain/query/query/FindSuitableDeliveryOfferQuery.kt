package me.rasztabiga.thesis.query.domain.query.query

data class FindSuitableDeliveryOfferQuery(
    val courierId: String,
    val courierAddress: String
)
