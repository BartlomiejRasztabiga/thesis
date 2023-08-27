package me.rasztabiga.thesis.order.domain.command.port

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

interface GeocodeAddressPort {

    fun geocode(address: String): Location
}
