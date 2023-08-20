package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*

class RestaurantOrderNotFoundException(id: UUID) : NoSuchElementException("RestaurantOrder with ID $id not found")
