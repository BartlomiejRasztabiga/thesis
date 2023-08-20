package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataOrderRepository : ReactiveMongoRepository<OrderEntity, UUID>
