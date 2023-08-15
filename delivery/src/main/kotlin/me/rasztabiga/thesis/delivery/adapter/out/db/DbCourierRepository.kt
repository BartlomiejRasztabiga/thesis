package me.rasztabiga.thesis.delivery.adapter.out.db

import me.rasztabiga.thesis.delivery.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.delivery.domain.query.repository.CourierRepository
import me.rasztabiga.thesis.delivery.infrastructure.db.SpringDataCourierRepository
import org.springframework.stereotype.Service

@Service
class DbCourierRepository(
    private val springDataCourierRepository: SpringDataCourierRepository
) : CourierRepository {
    override fun save(courier: CourierEntity) {
        springDataCourierRepository.save(courier).block()
    }

    override fun load(id: String): CourierEntity? {
        return springDataCourierRepository.findById(id).block()
    }
}
