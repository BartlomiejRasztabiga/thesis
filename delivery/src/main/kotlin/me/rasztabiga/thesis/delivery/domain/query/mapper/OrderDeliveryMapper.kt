package me.rasztabiga.thesis.delivery.domain.query.mapper

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.delivery.domain.command.aggregate.DeliveryStatus
import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity

object OrderDeliveryMapper {

    fun mapToEntity(event: OrderDeliveryCreatedEvent): OrderDeliveryEntity {
        return OrderDeliveryEntity(
            id = event.deliveryId,
            orderId = event.orderId,
            restaurantAddress = event.restaurantAddress,
            deliveryAddress = event.deliveryAddress,
            status = DeliveryStatus.OFFER,
            courierFee = event.courierFee,
            courierId = null,
            courierIdsDeclined = mutableListOf()
        )
    }

    fun mapToResponse(entity: OrderDeliveryEntity): OrderDeliveryResponse {
        return OrderDeliveryResponse(
            id = entity.id,
            restaurantAddress = entity.restaurantAddress,
            deliveryAddress = entity.deliveryAddress,
            status = OrderDeliveryResponse.DeliveryStatus.valueOf(entity.status.name),
            courierFee = entity.courierFee
        )
    }
}
