package me.rasztabiga.thesis.restaurant.domain.command.port

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

interface GeocodeAddressPort {

    fun geocode(address: String): Location
}
