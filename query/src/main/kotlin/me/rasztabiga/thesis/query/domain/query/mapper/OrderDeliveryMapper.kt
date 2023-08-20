package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent

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
