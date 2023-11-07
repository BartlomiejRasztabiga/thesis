package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity.Availability.ONLINE
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.ACCEPTED
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.ASSIGNED
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus.PICKED_UP
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
        val activeDeliveries = springDataOrderDeliveryRepository.findAllByStatusIn(
            setOf(
                ASSIGNED, ACCEPTED, PICKED_UP
            )
        ).collectList().block() ?: emptyList()
        val couriers =
            springDataCourierRepository.findAllByAvailabilityEqualsAndLocationIsNotNull(ONLINE).collectList().block()
                ?: emptyList()

        return couriers.filter { courier ->
            activeDeliveries.none { it.courierId == courier.id }
        }
    }
}
