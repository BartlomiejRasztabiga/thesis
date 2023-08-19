package me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper

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
}
