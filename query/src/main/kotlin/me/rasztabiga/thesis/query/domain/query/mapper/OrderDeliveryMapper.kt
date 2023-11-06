package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import java.time.Instant

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
            createdAt = Instant.now(),
            locked = false
        )
    }

    fun mapToResponse(entity: OrderDeliveryEntity): OrderDeliveryResponse {
        return OrderDeliveryResponse(
            id = entity.id,
            orderId = entity.orderId,
            restaurantLocation = entity.restaurantLocation,
            distanceToRestaurantInKm = null,
            deliveryLocation = entity.deliveryLocation,
            distanceToDeliveryAddressInKm = null,
            status = OrderDeliveryResponse.DeliveryStatus.valueOf(entity.status.name),
            courierFee = entity.courierFee,
            createdAt = entity.createdAt
        )
    }

    fun mapToResponse(
        entity: OrderDeliveryEntity,
        distanceToRestaurantInKm: Double,
        distanceToDeliveryAddressInKm: Double
    ): OrderDeliveryResponse {
        return OrderDeliveryResponse(
            id = entity.id,
            orderId = entity.orderId,
            restaurantLocation = entity.restaurantLocation,
            distanceToRestaurantInKm = distanceToRestaurantInKm,
            deliveryLocation = entity.deliveryLocation,
            distanceToDeliveryAddressInKm = distanceToDeliveryAddressInKm,
            status = OrderDeliveryResponse.DeliveryStatus.valueOf(entity.status.name),
            courierFee = entity.courierFee,
            createdAt = entity.createdAt
        )
    }
}
