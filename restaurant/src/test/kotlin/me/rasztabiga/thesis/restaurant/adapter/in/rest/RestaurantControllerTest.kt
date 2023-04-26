@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.restaurant.BaseWebFluxTest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.Availability
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantAvailabilityRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantRequest
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.restaurant.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.UuidWrapper
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import java.util.*

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
            .expectBody(UuidWrapper::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe request.id
    }

    @Test
    fun `when GET is performed on restaurants endpoint, then returns 200 OK`() {
        // given
        val existingRestaurant = RestaurantResponse(UUID.randomUUID(), "Restaurant", Availability.CLOSED)
        every {
            reactorQueryGateway.query(
                any<FindAllRestaurantsQuery>(),
                ResponseTypes.multipleInstancesOf(RestaurantResponse::class.java)
            )
        } returns Mono.just(listOf(existingRestaurant))

        // when
        val response = webTestClient.get()
            .uri("/api/v1/restaurants")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(RestaurantResponse::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.size shouldBe 1
        response[0].id shouldBe existingRestaurant.id
        response[0].name shouldBe existingRestaurant.name
    }

    @Test
    fun `when GET is performed on restaurant endpoint, then returns 200 OK`() {
        // given
        val existingRestaurant = RestaurantResponse(UUID.randomUUID(), "Restaurant", Availability.CLOSED)
        every {
            reactorQueryGateway.query(
                any<FindRestaurantByIdQuery>(),
                ResponseTypes.instanceOf(RestaurantResponse::class.java)
            )
        } returns Mono.just(existingRestaurant)

        // when
        val response = webTestClient.get()
            .uri("/api/v1/restaurants/${existingRestaurant.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(RestaurantResponse::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe existingRestaurant.id
        response.name shouldBe existingRestaurant.name
    }

    @Test
    fun `when PUT is performed on restaurant endpoint, then returns 200 OK`() {
        // given
        val restaurantId = UUID.randomUUID()

        val request = UpdateRestaurantRequest( "New name")
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(restaurantId)

        // when
        webTestClient.put()
            .uri("/api/v1/restaurants/${restaurantId}")
            .body(Mono.just(request), CreateRestaurantRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        // then
    }

    @Test
    fun `when DELETE is performed on restaurant endpoint, then returns 200 OK`() {
        // given
        val restaurantId = UUID.randomUUID()

        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(restaurantId)

        // when
        webTestClient.delete()
            .uri("/api/v1/restaurants/${restaurantId}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        // then
    }

    @Test
    fun `when PUT is performed on restaurant availability endpoint, then returns 200 OK`() {
        // given
        val restaurantId = UUID.randomUUID()

        val request = UpdateRestaurantAvailabilityRequest( Availability.OPEN)
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(restaurantId)

        // when
        webTestClient.put()
            .uri("/api/v1/restaurants/${restaurantId}/availability")
            .body(Mono.just(request), CreateRestaurantRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        // then
    }
}
