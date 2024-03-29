package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantOrderRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataRestaurantOrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class DbRestaurantOrderRepository(
    private val springDataRestaurantOrderRepository: SpringDataRestaurantOrderRepository
) : RestaurantOrderRepository {

    override fun save(restaurant: RestaurantOrderEntity) {
        springDataRestaurantOrderRepository.save(restaurant).block()
    }

    override fun loadAllByRestaurantId(restaurantId: UUID): Flux<RestaurantOrderEntity> {
        return springDataRestaurantOrderRepository.findAllByRestaurantId(restaurantId)
    }

    override fun load(id: UUID): RestaurantOrderEntity? {
        return springDataRestaurantOrderRepository.findById(id).block()
    }

    override fun loadByOrderId(orderId: UUID): RestaurantOrderEntity? {
        return springDataRestaurantOrderRepository.findByOrderId(orderId).block()
    }
}
