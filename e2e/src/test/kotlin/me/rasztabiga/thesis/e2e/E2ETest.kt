package me.rasztabiga.thesis.e2e

import io.kotest.matchers.equals.shouldBeEqual
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.specification.RequestSpecification
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierAvailability
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantAvailability
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateCourierAvailabilityRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateCourierLocationRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateRestaurantAvailabilityRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateRestaurantMenuRequest
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class E2ETest {

    private lateinit var restaurantManagerRequestSpecification: RequestSpecification
    private lateinit var orderingUserRequestSpecification: RequestSpecification
    private lateinit var courierRequestSpecification: RequestSpecification

    private lateinit var restaurantId: UUID
    private lateinit var userId: String
    private lateinit var courierId: String

    private lateinit var product1Id: UUID
    private lateinit var product2Id: UUID

    private lateinit var orderId: UUID
    private lateinit var stripeSessionUrl: String

    private lateinit var driver: ChromeDriver

    private val log: Logger = LoggerFactory.getLogger(E2ETest::class.java)

    @BeforeEach
    fun setUp() {
        val baseUri = "https://thesis.rasztabiga.me/api/v1"

        restaurantManagerRequestSpecification = RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setAuth(RestAssured.oauth2(getRestaurantManagerToken()))
            .setContentType("application/json")
            .build()

        orderingUserRequestSpecification = RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setAuth(RestAssured.oauth2(getOrderingUserToken()))
            .setContentType("application/json")
            .build()

        courierRequestSpecification = RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setAuth(RestAssured.oauth2(getCourierToken()))
            .setContentType("application/json")
            .build()

        val responseSpecification = ResponseSpecBuilder()
            .expectResponseTime(lessThan(2L), TimeUnit.SECONDS)
            .build()

        RestAssured.responseSpecification = responseSpecification

        driver = ChromeDriver()
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500))
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun e2e() {
        setupRestaurant()
        setupOrderingUser()
        setupCourier()

        createOrder()
        payOrder()
    }

    private fun setupRestaurant() {
        createOrUseExistingRestaurant()
        updateRestaurantMenu()
        updateRestaurantAvailability()
    }

    private fun setupOrderingUser() {
        createOrUseExistingUser()
    }

    private fun setupCourier() {
        createOrUseExistingCourier()
        updateCourierAvailability()
        updateCourierLocation()
    }

    private fun createOrder() {
        startOrder()
        addOrderItems()
        finalizeOrder()

        // sleep for 5 seconds to let the order be processed by stripe
        Thread.sleep(5000)

        setStripeSessionUrl()
    }

    private fun payOrder() {
        driver.get(stripeSessionUrl)

        driver.findElement(By.id("email")).sendKeys("test@example.com")
        driver.findElement(By.id("cardNumber")).sendKeys("4242424242424242")
        driver.findElement(By.id("cardExpiry")).sendKeys("1225")
        driver.findElement(By.id("cardCvc")).sendKeys("123")
        driver.findElement(By.id("billingName")).sendKeys("Test User")
        driver.findElement(By.cssSelector("button[type='submit']")).click()

        driver.quit()

        // sleep for 5 seconds to let the order be processed by payments service
        Thread.sleep(5000)

        given(orderingUserRequestSpecification)
            .`when`()
            .get("/orders/$orderId")
            .then()
            .statusCode(200)
            .extract()
            .path<String>("status").shouldBeEqual("PAID")
    }

    private fun createOrUseExistingRestaurant() {
        // try to get restaurant for current user
        val restaurantResponse = given(restaurantManagerRequestSpecification)
            .`when`()
            .get("/restaurants/me")

        if (restaurantResponse.statusCode == 200) {
            restaurantId = restaurantResponse
                .then()
                .extract()
                .path<String>("id").let { UUID.fromString(it) }

            log.info("Restaurant already exists: $restaurantId")
        } else {
            // create restaurant
            val createRestaurantRequest = CreateRestaurantRequest(
                id = UUID.randomUUID(),
                name = "Restaurant",
                address = "Cypryjska 70, 02-762 Warszawa",
                email = "bartlomiej.rasztabiga.official+restaurant@gmail.com",
                imageUrl = "https://mui.com/static/images/cards/contemplative-reptile.jpg"
            )

            restaurantId = given(restaurantManagerRequestSpecification)
                .body(createRestaurantRequest)
                .`when`()
                .post("/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path<String>("id").let { UUID.fromString(it) }

            log.info("Restaurant created: $restaurantId")
        }
    }

    private fun updateRestaurantMenu() {
        val request = UpdateRestaurantMenuRequest(
            menu = listOf(
                UpdateRestaurantMenuRequest.Product(
                    name = "Burger",
                    description = "with extra cheese",
                    price = 21.37.toBigDecimal(),
                    imageUrl = "https://www.foodiesfeed.com/wp-content/uploads/2023/04/cheeseburger.jpg"
                ),
                UpdateRestaurantMenuRequest.Product(
                    name = "Milkshake",
                    description = "with extra strawberries",
                    price = 23.37.toBigDecimal(),
                    imageUrl = "https://www.foodiesfeed.com/wp-content/uploads/2023/04/strawberry-milk-splash.jpg"
                )
            )
        )

        given(restaurantManagerRequestSpecification)
            .body(request)
            .`when`()
            .put("/restaurants/$restaurantId/menu")
            .then()
            .statusCode(200)

        val menu = given(restaurantManagerRequestSpecification)
            .`when`()
            .get("/restaurants/me")
            .body.`as`(RestaurantResponse::class.java)

        product1Id = menu.menu[0].id
        product2Id = menu.menu[1].id

        log.info("Restaurant menu updated")
    }

    private fun updateRestaurantAvailability() {
        val request = UpdateRestaurantAvailabilityRequest(
            availability = RestaurantAvailability.OPEN
        )

        given(restaurantManagerRequestSpecification)
            .body(request)
            .`when`()
            .put("/restaurants/$restaurantId/availability")
            .then()
            .statusCode(200)

        log.info("Restaurant availability updated")
    }

    private fun createOrUseExistingUser() {
        // try to get current user
        val userResponse = given(orderingUserRequestSpecification)
            .`when`()
            .get("/users/me")

        if (userResponse.statusCode == 200) {
            userId = userResponse
                .then()
                .extract()
                .path("id")

            if (userResponse.then().extract().path<List<Any>>("deliveryAddresses").isEmpty()) {
                createDeliveryAddress()
            }

            log.info("User already exists: $userId")
        } else {
            val request = CreateUserRequest(
                name = "User",
                email = "bartlomiej.rasztabiga.official+user@gmail.com"
            )

            userId = given(orderingUserRequestSpecification)
                .body(request)
                .`when`()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id")

            createDeliveryAddress()

            log.info("User created: $userId")
        }
    }

    private fun createDeliveryAddress() {
        val request = CreateDeliveryAddressRequest(
            address = "Bukowińska 26C, 02-703 Warszawa",
            additionalInfo = null
        )

        given(orderingUserRequestSpecification)
            .body(request)
            .`when`()
            .post("/users/$userId/addresses")
            .then()
            .statusCode(201)

        log.info("Delivery address created")
    }

    private fun createOrUseExistingCourier() {
        // try to get current courier
        val courierResponse = given(courierRequestSpecification)
            .`when`()
            .get("/couriers/me")

        if (courierResponse.statusCode == 200) {
            courierId = courierResponse
                .then()
                .extract()
                .path("id")

            log.info("Courier already exists: $courierId")
        } else {
            val request = CreateUserRequest(
                name = "Courier",
                email = "bartlomiej.rasztabiga.official+courier@gmail.com"
            )

            courierId = given(courierRequestSpecification)
                .body(request)
                .`when`()
                .post("/couriers")
                .then()
                .statusCode(201)
                .extract()
                .path("id")

            log.info("Courier created: $courierId")
        }
    }

    private fun updateCourierAvailability() {
        val request = UpdateCourierAvailabilityRequest(
            availability = CourierAvailability.ONLINE
        )

        given(courierRequestSpecification)
            .body(request)
            .`when`()
            .put("/couriers/me/availability")
            .then()
            .statusCode(200)

        log.info("Courier availability updated")
    }

    private fun updateCourierLocation() {
        val request = UpdateCourierLocationRequest(
            location = Location(lat = 52.181564, lng = 21.026544, streetAddress = null)
        )

        given(courierRequestSpecification)
            .body(request)
            .`when`()
            .put("/couriers/me/location")
            .then()
            .statusCode(200)

        log.info("Courier location updated")
    }

    private fun startOrder() {
        val request = StartOrderRequest(
            restaurantId = restaurantId,
        )

        orderId = given(orderingUserRequestSpecification)
            .body(request)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id").let { UUID.fromString(it) }

        log.info("Order started: $orderId")
    }

    private fun addOrderItems() {
        var request = AddOrderItemRequest(
            productId = product1Id
        )

        given(orderingUserRequestSpecification)
            .body(request)
            .`when`()
            .post("/orders/$orderId/items")
            .then()
            .statusCode(201)

        request = AddOrderItemRequest(
            productId = product2Id
        )

        given(orderingUserRequestSpecification)
            .body(request)
            .`when`()
            .post("/orders/$orderId/items")
            .then()
            .statusCode(201)

        log.info("Order items added")
    }

    private fun finalizeOrder() {
        given(orderingUserRequestSpecification)
            .`when`()
            .put("/orders/$orderId/finalize")
            .then()
            .statusCode(200)

        log.info("Order finalized")
    }

    private fun setStripeSessionUrl() {
        val response = given(orderingUserRequestSpecification)
            .`when`()
            .get("/orders/$orderId")
            .body.`as`(OrderResponse::class.java)

        requireNotNull(response.paymentSessionUrl)

        stripeSessionUrl = response.paymentSessionUrl!!

        log.info("Stripe session url set: $stripeSessionUrl")
    }

    private fun getRestaurantManagerToken(): String {
        return given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .accept("application/json")
            .formParam("grant_type", "password")
            .formParam("audience", "https://thesis.rasztabiga.me/api")
            .formParam("username", "restaurant@thesis.rasztabiga.me")
            .formParam("password", "sazni3-docmaj-foNnuz")
            .formParam("client_id", "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4")
            .formParam("client_secret", "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu")
            .formParam("scope", "read:restaurants write:restaurants read:users write:users")
            .`when`()
            .post("https://rasztabigab.eu.auth0.com/oauth/token")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .path("access_token")
    }

    private fun getOrderingUserToken(): String {
        return given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .accept("application/json")
            .formParam("grant_type", "password")
            .formParam("audience", "https://thesis.rasztabiga.me/api")
            .formParam("username", "user@thesis.rasztabiga.me")
            .formParam("password", "qabdyq-kepTed-xikke2")
            .formParam("client_id", "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4")
            .formParam("client_secret", "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu")
            .formParam("scope", "read:users write:users read:orders write:orders")
            .`when`()
            .post("https://rasztabigab.eu.auth0.com/oauth/token")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .path("access_token")
    }

    private fun getCourierToken(): String {
        return given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .accept("application/json")
            .formParam("grant_type", "password")
            .formParam("audience", "https://thesis.rasztabiga.me/api")
            .formParam("username", "delivery@thesis.rasztabiga.me")
            .formParam("password", "Fuzpa9-sygfyx-xurjif")
            .formParam("client_id", "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4")
            .formParam("client_secret", "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu")
            .formParam("scope", "read:deliveries write:deliveries read:couriers write:couriers read:users write:users")
            .`when`()
            .post("https://rasztabigab.eu.auth0.com/oauth/token")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .path("access_token")
    }
}
