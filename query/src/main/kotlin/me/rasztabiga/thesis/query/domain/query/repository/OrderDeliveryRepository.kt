package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import reactor.core.publisher.Flux
import java.util.*

interface OrderDeliveryRepository {
    fun save(delivery: OrderDeliveryEntity)

    fun load(id: UUID): OrderDeliveryEntity?

    fun loadOffers(): List<OrderDeliveryEntity>

    fun loadCurrentDeliveryByCourierId(courierId: String): OrderDeliveryEntity?

    fun loadAllByCourierId(courierId: String): Flux<OrderDeliveryEntity>
}
