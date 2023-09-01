@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.shared.BaseWebFluxTest
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import java.util.*

class UserControllerTest : BaseWebFluxTest() {

    @Test
    fun `when POST is performed on users endpoint, then returns 201 CREATED`() {
        // given
        val request = CreateUserRequest("User", "email")
        every { reactorCommandGateway.send<String>(any()) } returns Mono.just("1")

        // when
        val response = webTestClient.post()
            .uri("/api/v1/users")
            .body(Mono.just(request), CreateUserRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(StringIdWrapper::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe "1"
    }

    @Test
    fun `when POST is performed on delivery addresses endpoint, then returns 201 CREATED`() {
        // given
        val request = CreateDeliveryAddressRequest("address", "additionalInfo")
        every { reactorCommandGateway.send<UUID>(any()) } returns Mono.just(UUID.randomUUID())

        // when
        val response = webTestClient.post()
            .uri("/api/v1/users/1/addresses")
            .body(Mono.just(request), CreateUserRequest::class.java)
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
    fun `when DELETE is performed on delivery addresses endpoint, then returns 204 NO_CONTENT`() {
        // given
        val addressId = UUID.randomUUID()
        every { reactorCommandGateway.send<Void>(any()) } returns Mono.empty()

        // when
        webTestClient.delete()
            .uri("/api/v1/users/1/addresses/$addressId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // then
    }
}
