package me.rasztabiga.thesis.restaurant.domain.query

import java.util.*
import kotlin.reflect.full.memberProperties

open class BaseInMemoryRepository<T : Any> {

    private val entities = mutableMapOf<UUID, T>()

    protected fun addEntity(entity: T) {
        val id = entity::class.memberProperties.find { it.name == "id" }?.call(entity) as UUID
        entities[id] = entity
    }

    protected fun loadEntity(id: UUID): T? {
        return entities[id]
    }

    protected fun loadAllEntities(): List<T> {
        return entities.values.toList()
    }

    protected fun deleteEntity(id: UUID) {
        entities.remove(id)
    }
}
