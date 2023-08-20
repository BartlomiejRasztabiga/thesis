package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity
import java.util.*

interface OrderDeliveryRepository {
    fun save(delivery: OrderDeliveryEntity)

    fun load(id: UUID): OrderDeliveryEntity?

    fun loadOffers(): List<OrderDeliveryEntity>

    fun loadCurrentDeliveryByCourierId(courierId: String): OrderDeliveryEntity?
}
