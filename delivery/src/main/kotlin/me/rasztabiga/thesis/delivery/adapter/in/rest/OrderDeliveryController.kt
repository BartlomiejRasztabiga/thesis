@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.delivery.adapter.`in`.rest

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.OrderDeliveryControllerMapper.mapToAcceptDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.OrderDeliveryControllerMapper.mapToDeliverDeliveryCommand
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.OrderDeliveryControllerMapper.mapToPickupDeliveryCommand
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.OrderDeliveryControllerMapper.mapToRejectDeliveryOfferCommand
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes.COURIER
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/deliveries")
class OrderDeliveryController(
    private val reactorCommandGateway: ReactorCommandGateway
) {
    @PutMapping("/{deliveryId}/reject")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun rejectDeliveryOffer(
        @PathVariable deliveryId: UUID,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToRejectDeliveryOfferCommand(deliveryId, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{deliveryId}/accept")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun acceptDeliveryOffer(
        @PathVariable deliveryId: UUID,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToAcceptDeliveryOfferCommand(deliveryId, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{deliveryId}/pickup")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun pickupDelivery(
        @PathVariable deliveryId: UUID,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToPickupDeliveryCommand(deliveryId, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{deliveryId}/deliver")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun deliverDelivery(
        @PathVariable deliveryId: UUID,
        exchange: ServerWebExchange
    ): Mono<UuidWrapper> {
        val command = mapToDeliverDeliveryCommand(deliveryId, exchange)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }
}
