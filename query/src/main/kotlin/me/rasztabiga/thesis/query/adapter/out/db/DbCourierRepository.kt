package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity.Availability.ONLINE
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataCourierRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataOrderDeliveryRepository
import org.springframework.stereotype.Service

@Service
class DbCourierRepository(
    private val springDataCourierRepository: SpringDataCourierRepository,
    private val springDataOrderDeliveryRepository: SpringDataOrderDeliveryRepository
) : CourierRepository {
    override fun save(courier: CourierEntity) {
        springDataCourierRepository.save(courier).block()
    }

    override fun load(id: String): CourierEntity? {
        return springDataCourierRepository.findById(id).block()
    }

    override fun loadAllOnlineWithoutCurrentDelivery(): List<CourierEntity> {
        val deliveries = springDataOrderDeliveryRepository.findAll().collectList().block() ?: emptyList()
        val couriers =
            springDataCourierRepository.findAllByAvailabilityEqualsAndLocationIsNotNull(ONLINE).collectList().block()
                ?: emptyList()

        return couriers.filter { courier ->
            deliveries.none { it.courierId == courier.id }
        }
    }
}
