package me.rasztabiga.thesis.delivery.domain.query.exception

class CourierNotFoundException(id: String) : NoSuchElementException("Courier with ID $id not found")
