package me.rasztabiga.thesis.restaurant.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.restaurant.BaseWebFluxTest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.shared.UuidWrapper
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import java.util.UUID

class RestaurantControllerTest : BaseWebFluxTest() {

    @Test
    fun `when POST is performed on restaurants endpoint, then returns 201 CREATED`() {
        // given
        val request = CreateRestaurantRequest(UUID.randomUUID(), "Restaurant")
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(request.id)

        // when
        val response = webTestClient.post()
            .uri("/api/v1/restaurants")
            .body(Mono.just(request), CreateRestaurantRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .returnResult(UuidWrapper::class.java)
            .responseBody
            .blockFirst()

        // then
        response shouldNotBe null
        response!!.id shouldBe request.id
    }
}
