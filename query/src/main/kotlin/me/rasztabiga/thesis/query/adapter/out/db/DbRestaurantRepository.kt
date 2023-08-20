package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataRestaurantRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class DbRestaurantRepository(
    private val springDataRestaurantRepository: SpringDataRestaurantRepository
) : RestaurantRepository {

    override fun save(restaurant: RestaurantEntity) {
        springDataRestaurantRepository.save(restaurant).block()
    }

    override fun load(id: UUID): RestaurantEntity? {
        return springDataRestaurantRepository.findById(id).block()
    }

    override fun loadAll(): Flux<RestaurantEntity> {
        return springDataRestaurantRepository.findAll()
    }

    override fun delete(id: UUID) {
        springDataRestaurantRepository.deleteById(id).block()
    }
}
