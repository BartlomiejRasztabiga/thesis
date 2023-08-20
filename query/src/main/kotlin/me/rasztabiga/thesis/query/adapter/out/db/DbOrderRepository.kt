package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.query.domain.query.repository.OrderRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataOrderRepository
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
