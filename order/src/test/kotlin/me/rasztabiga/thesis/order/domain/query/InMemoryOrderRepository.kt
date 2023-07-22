package me.rasztabiga.thesis.order.domain.query

import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.order.domain.query.repository.OrderRepository
import java.util.*

class InMemoryOrderRepository : OrderRepository,
    BaseInMemoryRepository<OrderEntity>() {

    override fun save(order: OrderEntity) {
        addEntity(order)
    }

    override fun load(id: UUID): OrderEntity? {
        return loadEntity(id)
    }
}
