package me.rasztabiga.thesis.order.adapter.out.db

import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.order.domain.query.repository.OrderRepository
import me.rasztabiga.thesis.order.infrastructure.db.SpringDataOrderRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DbOrderRepository(
    private val springDataOrderRepository: SpringDataOrderRepository
) : OrderRepository {

    override fun save(order: OrderEntity) {
        springDataOrderRepository.save(order).block()
    }

    override fun load(id: UUID): OrderEntity? {
        return springDataOrderRepository.findById(id).block()
    }
}
