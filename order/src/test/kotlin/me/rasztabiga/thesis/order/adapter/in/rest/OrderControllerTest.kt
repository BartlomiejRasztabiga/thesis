@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.shared.BaseWebFluxTest
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

class OrderControllerTest : BaseWebFluxTest() {

    @Test
    fun `when POST is performed on orders endpoint, then returns 201 CREATED`() {
        // given
        val request = StartOrderRequest(UUID.randomUUID())
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
    fun `when POST is performed on order items endpoint, then returns 201 CREATED`() {
        // given
        val request = AddOrderItemRequest(UUID.randomUUID())
        val orderId = UUID.randomUUID()
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(orderId)

        // when
        val response = webTestClient.post()
            .uri("/api/v1/orders/$orderId/items")
            .body(Mono.just(request), StartOrderRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(UuidWrapper::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldNotBe null
    }

    @Test
    fun `when DELETE is performed on order items endpoint, then returns 204 NO_CONTENT`() {
        // given
        val orderId = UUID.randomUUID()
        val orderItemId = UUID.randomUUID()
        every { reactorCommandGateway.send<Void>(any()) } returns Mono.empty()

        // when
        webTestClient.delete()
            .uri("/api/v1/orders/$orderId/items/$orderItemId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // then
    }

    @Test
    fun `when DELETE is performed on order endpoint, then returns 204 NO_CONTENT`() {
        // given
        val orderId = UUID.randomUUID()
        every { reactorCommandGateway.send<Void>(any()) } returns Mono.empty()

        // when
        webTestClient.delete()
            .uri("/api/v1/orders/$orderId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // then
    }
}
