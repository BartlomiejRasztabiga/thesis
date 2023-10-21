package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*

class RestaurantNotFoundForManagerIdException(id: String) : NoSuchElementException("Restaurant not found for manager id: $id")
