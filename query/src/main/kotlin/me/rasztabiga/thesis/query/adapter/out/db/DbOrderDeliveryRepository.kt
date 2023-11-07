package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.ACCEPTED
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.ASSIGNED
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.PICKED_UP
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.query.domain.query.repository.OrderDeliveryRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataOrderDeliveryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
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

    override fun loadOffers(): List<OrderDeliveryEntity> {
        return springDataOrderDeliveryRepository.findAllByStatus(DeliveryStatus.OFFER).collectList()
            .block() ?: listOf()
    }

    override fun loadCurrentDeliveryByCourierId(courierId: String): OrderDeliveryEntity? {
        return springDataOrderDeliveryRepository.findFirstByCourierIdAndStatusIn(
            courierId,
            setOf(ASSIGNED, ACCEPTED, PICKED_UP)
        ).block()
    }

    override fun loadAllByCourierId(courierId: String): Flux<OrderDeliveryEntity> {
        return springDataOrderDeliveryRepository.findByCourierId(courierId)
    }
}
