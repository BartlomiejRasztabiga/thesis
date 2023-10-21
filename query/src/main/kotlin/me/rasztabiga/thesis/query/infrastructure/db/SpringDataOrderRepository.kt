package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface SpringDataOrderRepository : ReactiveMongoRepository<OrderEntity, UUID> {

    fun findAllByUserId(userId: String): Flux<OrderEntity>
}
