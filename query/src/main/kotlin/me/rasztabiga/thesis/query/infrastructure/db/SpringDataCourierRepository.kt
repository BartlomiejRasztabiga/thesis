package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface SpringDataCourierRepository : ReactiveMongoRepository<CourierEntity, String> {
    fun findAllByAvailabilityEqualsAndLocationIsNotNull(availability: CourierEntity.Availability): Flux<CourierEntity>
}
