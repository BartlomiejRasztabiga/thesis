package me.rasztabiga.thesis.order.domain.query.exception

import java.util.*

class OrderNotFoundException(id: UUID) : NoSuchElementException("Order with ID $id not found")
