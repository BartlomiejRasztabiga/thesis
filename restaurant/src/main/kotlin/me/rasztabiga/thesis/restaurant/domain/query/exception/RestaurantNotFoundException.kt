package me.rasztabiga.thesis.restaurant.domain.query.exception

import java.util.*

class RestaurantNotFoundException(id: UUID) : NoSuchElementException("Restaurant with ID $id not found")
