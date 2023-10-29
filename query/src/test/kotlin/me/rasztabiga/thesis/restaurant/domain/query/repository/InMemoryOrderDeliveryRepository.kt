package me.rasztabiga.thesis.restaurant.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.query.domain.query.repository.OrderDeliveryRepository
import me.rasztabiga.thesis.shared.BaseInMemoryRepository
import reactor.core.publisher.Flux
import java.util.*

class InMemoryOrderDeliveryRepository : OrderDeliveryRepository,
    BaseInMemoryRepository<OrderDeliveryEntity>() {

    override fun save(delivery: OrderDeliveryEntity) {
        addEntity(delivery)
    }

    override fun load(id: UUID): OrderDeliveryEntity? {
        return loadEntity(id)
    }

    override fun loadOffers(): List<OrderDeliveryEntity> {
        return loadAllEntities().filter { it.status == DeliveryStatus.OFFER }
    }

    override fun loadCurrentDeliveryByCourierId(courierId: String): OrderDeliveryEntity? {
        val orders = loadAllEntities().filter { it.courierId == courierId }
        return orders.firstOrNull { it.status == DeliveryStatus.ACCEPTED || it.status == DeliveryStatus.PICKED_UP }
    }

    override fun loadAllByCourierId(courierId: String): Flux<OrderDeliveryEntity> {
        return Flux.fromIterable(loadAllEntities().filter { it.courierId == courierId })
    }
}
