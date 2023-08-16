package me.rasztabiga.thesis.restaurant.config.upcaster

import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantOrderCreatedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Suppress("ClassNaming")
@Component
@Order(1)
class RestaurantCreatedEvent1_to_2Upcaster : SingleEventUpcaster() {

    companion object {
        private val TARGET_TYPE = SimpleSerializedType(
            RestaurantCreatedEvent::class.java.typeName,
            "1.0"
        )
    }

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == TARGET_TYPE
    }

    override fun doUpcast(
        intermediateRepresentation: IntermediateEventRepresentation
    ): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            SimpleSerializedType(TARGET_TYPE.name, "2.0"),
            Document::class.java
        ) { document: Document ->
            document.rootElement.addElement("address").text = ""
            document
        }
    }
}
