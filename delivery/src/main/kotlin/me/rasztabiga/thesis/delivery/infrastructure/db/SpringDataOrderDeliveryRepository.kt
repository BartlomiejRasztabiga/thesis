package me.rasztabiga.thesis.delivery.infrastructure.db

import me.rasztabiga.thesis.delivery.domain.command.aggregate.DeliveryStatus
import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SpringDataOrderDeliveryRepository : ReactiveMongoRepository<OrderDeliveryEntity, UUID> {
    fun findAllByStatus(status: DeliveryStatus): Flux<OrderDeliveryEntity>

    fun findByCourierId(courierId: String): Flux<OrderDeliveryEntity>
}
