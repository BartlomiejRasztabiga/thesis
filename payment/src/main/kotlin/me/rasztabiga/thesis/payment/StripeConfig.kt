package me.rasztabiga.thesis.payment

import com.stripe.Stripe
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("stripe")
@Component
class StripeConfig(
    @Value("\${stripe.api.key}")
    private val stripeApiKey: String
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        Stripe.apiKey = stripeApiKey
    }
}
