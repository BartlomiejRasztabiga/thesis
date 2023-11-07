package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SpringDataOrderDeliveryRepository : ReactiveMongoRepository<OrderDeliveryEntity, UUID> {
    fun findAllByStatusAndLockedIsFalse(status: DeliveryStatus): Flux<OrderDeliveryEntity>

    fun findByCourierId(courierId: String): Flux<OrderDeliveryEntity>

    fun findFirstByCourierIdAndStatusIn(courierId: String, statuses: Set<DeliveryStatus>): Mono<OrderDeliveryEntity>
}
