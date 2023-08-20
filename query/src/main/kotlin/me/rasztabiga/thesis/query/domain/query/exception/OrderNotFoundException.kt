package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*

class OrderNotFoundException(id: UUID) : NoSuchElementException("Order with ID $id not found")
