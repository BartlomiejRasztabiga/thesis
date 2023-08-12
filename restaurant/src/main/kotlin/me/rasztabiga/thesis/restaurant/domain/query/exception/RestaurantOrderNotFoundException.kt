package me.rasztabiga.thesis.restaurant.domain.query.exception

import java.util.*

class RestaurantOrderNotFoundException(id: UUID) : NoSuchElementException("RestaurantOrder with ID $id not found")
