package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.OrderDeliveryOfferResponse
import me.rasztabiga.thesis.query.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent

object OrderDeliveryMapper {

    fun mapToEntity(event: OrderDeliveryCreatedEvent): OrderDeliveryEntity {
        return OrderDeliveryEntity(
            id = event.deliveryId,
            orderId = event.orderId,
            restaurantLocation = event.restaurantLocation,
            deliveryLocation = event.deliveryLocation,
            status = DeliveryStatus.OFFER,
            courierFee = event.courierFee,
            courierId = null,
            courierIdsDeclined = mutableListOf()
        )
    }

    fun mapToResponse(entity: OrderDeliveryEntity): OrderDeliveryResponse {
        return OrderDeliveryResponse(
            id = entity.id,
            restaurantLocation = entity.restaurantLocation,
            deliveryLocation = entity.deliveryLocation,
            status = OrderDeliveryResponse.DeliveryStatus.valueOf(entity.status.name),
            courierFee = entity.courierFee
        )
    }

    fun mapToResponse(
        entity: OrderDeliveryEntity,
        distanceToRestaurantInKm: Double,
        distanceToDeliveryAddressInKm: Double
    ): OrderDeliveryOfferResponse {
        return OrderDeliveryOfferResponse(
            id = entity.id,
            restaurantLocation = entity.restaurantLocation,
            distanceToRestaurantInKm = distanceToRestaurantInKm,
            deliveryLocation = entity.deliveryLocation,
            distanceToDeliveryAddressInKm = distanceToDeliveryAddressInKm,
            status = OrderDeliveryOfferResponse.DeliveryStatus.valueOf(entity.status.name),
            courierFee = entity.courierFee
        )
    }
}
