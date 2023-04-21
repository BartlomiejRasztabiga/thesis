package me.rasztabiga.thesis.restaurant

import com.ninjasquad.springmockk.MockkBean
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.RestaurantController
import me.rasztabiga.thesis.restaurant.config.TestSecurityConfig
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [RestaurantController::class])
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
open class BaseWebFluxTest {

    @MockkBean
    protected lateinit var reactorCommandGateway: ReactorCommandGateway

    @MockkBean
    protected lateinit var reactorQueryGateway: ReactorQueryGateway

    @Autowired
    protected lateinit var webTestClient: WebTestClient
}
