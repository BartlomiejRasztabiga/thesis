package me.rasztabiga.thesis.query.domain.query.exception

class RestaurantNotFoundForManagerIdException(id: String) :
    NoSuchElementException("Restaurant not found for manager id: $id")
