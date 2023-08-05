package me.rasztabiga.thesis.shared

import kotlin.reflect.full.memberProperties

open class BaseInMemoryRepository<T : Any> {

    private val entities = mutableMapOf<Any, T>()

    protected fun addEntity(entity: T) {
        val id = entity::class.memberProperties.find { it.name == "id" }?.call(entity) as Any
        entities[id] = entity
    }

    protected fun loadEntity(id: Any): T? {
        return entities[id]
    }

    protected fun loadAllEntities(): List<T> {
        return entities.values.toList()
    }

    protected fun deleteEntity(id: Any) {
        entities.remove(id)
    }
}
