package me.rasztabiga.thesis.order.adapter.out.gmaps

import me.rasztabiga.thesis.order.domain.command.port.GeocodeAddressPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.infrastructure.gmaps.GmapsClient
import org.springframework.stereotype.Service

@Service
class GmapsGeocodeAddressAdapter(
    private val gmapsClient: GmapsClient
) : GeocodeAddressPort {

    override fun geocode(address: String): Location {
        return gmapsClient.geocode(address)
    }
}
