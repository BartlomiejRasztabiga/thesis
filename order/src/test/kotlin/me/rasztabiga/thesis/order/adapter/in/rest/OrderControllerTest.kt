@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.order.BaseWebFluxTest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.UuidWrapper
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import java.util.*

class OrderControllerTest : BaseWebFluxTest() {

    @Test
    fun `when POST is performed on orders endpoint, then returns 201 CREATED`() {
        // given
        val request = StartOrderRequest("1", UUID.randomUUID())
        val orderId = UUID.randomUUID()
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(orderId)

        // when
        val response = webTestClient.post()
            .uri("/api/v1/orders")
            .body(Mono.just(request), StartOrderRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(UuidWrapper::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe orderId
    }

    @Test
    fun `when GET is performed on order endpoint, then returns 200 OK`() {
        // given
        val existingOrder = OrderResponse(UUID.randomUUID(), OrderResponse.OrderStatus.CREATED)
        every {
            reactorQueryGateway.query(
                any<FindOrderByIdQuery>(),
                ResponseTypes.instanceOf(OrderResponse::class.java)
            )
        } returns Mono.just(existingOrder)

        // when
        val response = webTestClient.get()
            .uri("/api/v1/orders/${existingOrder.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(OrderResponse::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe existingOrder.id
        response.status shouldBe existingOrder.status
    }
}
