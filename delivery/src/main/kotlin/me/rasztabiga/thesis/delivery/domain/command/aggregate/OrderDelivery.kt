package me.rasztabiga.thesis.delivery.domain.command.aggregate

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
class OrderDelivery {

    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var orderId: UUID
    private lateinit var restaurantAddress: String
    private lateinit var deliveryAddress: String
    private var status: DeliveryStatus = DeliveryStatus.OFFER
    private var courierId: String? = null
    private var courierFee: BigDecimal? = null

    private constructor()

    // TODO inject fee calculator
}
