package me.rasztabiga.thesis.delivery.infrastructure.db

import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataOrderDeliveryRepository : ReactiveMongoRepository<OrderDeliveryEntity, UUID>
