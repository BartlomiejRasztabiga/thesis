package me.rasztabiga.thesis.delivery.adapter.out.db

import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.delivery.domain.query.repository.OrderDeliveryRepository
import me.rasztabiga.thesis.delivery.infrastructure.db.SpringDataOrderDeliveryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DbOrderDeliveryRepository(
    private val springDataOrderDeliveryRepository: SpringDataOrderDeliveryRepository
) : OrderDeliveryRepository {
    override fun save(delivery: OrderDeliveryEntity) {
        springDataOrderDeliveryRepository.save(delivery).block()
    }

    override fun load(id: UUID): OrderDeliveryEntity? {
        return springDataOrderDeliveryRepository.findById(id).block()
    }
}
