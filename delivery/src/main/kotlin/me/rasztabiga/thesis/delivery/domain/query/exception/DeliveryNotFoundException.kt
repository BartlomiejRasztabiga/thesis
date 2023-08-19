package me.rasztabiga.thesis.delivery.domain.query.exception

import java.util.UUID

class DeliveryNotFoundException(id: UUID) : NoSuchElementException("Delivery with ID $id not found")
