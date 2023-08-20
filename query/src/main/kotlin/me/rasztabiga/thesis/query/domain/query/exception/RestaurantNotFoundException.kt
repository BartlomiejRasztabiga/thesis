package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*

class RestaurantNotFoundException(id: UUID) : NoSuchElementException("Restaurant with ID $id not found")
