package me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.delivery.domain.command.command.AcceptDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.command.DeliverDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.PickupDeliveryCommand
import me.rasztabiga.thesis.delivery.domain.command.command.RejectDeliveryOfferCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object OrderDeliveryControllerMapper {

    fun mapToRejectDeliveryOfferCommand(
        deliveryId: UUID,
        exchange: ServerWebExchange
    ): RejectDeliveryOfferCommand {
        return RejectDeliveryOfferCommand(
            id = deliveryId,
            courierId = exchange.getUserId()
        )
    }

    fun mapToAcceptDeliveryOfferCommand(
        deliveryId: UUID,
        exchange: ServerWebExchange
    ): AcceptDeliveryOfferCommand {
        return AcceptDeliveryOfferCommand(
            id = deliveryId,
            courierId = exchange.getUserId()
        )
    }

    fun mapToPickupDeliveryCommand(
        deliveryId: UUID,
        exchange: ServerWebExchange
    ): PickupDeliveryCommand {
        return PickupDeliveryCommand(
            id = deliveryId,
            courierId = exchange.getUserId()
        )
    }

    fun mapToDeliverDeliveryCommand(
        deliveryId: UUID,
        exchange: ServerWebExchange
    ): DeliverDeliveryCommand {
        return DeliverDeliveryCommand(
            id = deliveryId,
            courierId = exchange.getUserId()
        )
    }
}
