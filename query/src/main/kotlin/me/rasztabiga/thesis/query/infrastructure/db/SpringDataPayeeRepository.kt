package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface SpringDataPayeeRepository : ReactiveMongoRepository<PayeeEntity, UUID> {

    fun findByUserId(userId: String): Mono<PayeeEntity>
}
