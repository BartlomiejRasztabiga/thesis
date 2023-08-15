package me.rasztabiga.thesis.delivery.infrastructure.db

import me.rasztabiga.thesis.delivery.domain.query.entity.CourierEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataCourierRepository : ReactiveMongoRepository<CourierEntity, String>
