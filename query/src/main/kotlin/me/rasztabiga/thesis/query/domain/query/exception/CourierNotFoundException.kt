package me.rasztabiga.thesis.query.domain.query.exception

class CourierNotFoundException(id: String) : NoSuchElementException("Courier with ID $id not found")
