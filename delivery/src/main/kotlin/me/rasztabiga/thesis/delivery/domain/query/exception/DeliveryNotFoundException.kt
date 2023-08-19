package me.rasztabiga.thesis.delivery.domain.query.exception

import java.util.*

class DeliveryNotFoundException : NoSuchElementException {
    constructor() : super("Delivery not found")
    constructor(id: UUID) : super("Delivery with id $id not found")
}
