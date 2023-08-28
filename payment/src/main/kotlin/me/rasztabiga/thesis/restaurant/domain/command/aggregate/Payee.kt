package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate
internal class Payee {
    // either restaurant manager or courier, both have some balance and can withdraw money

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String

    private lateinit var balance: BigDecimal

    private constructor()

    // TODO create

    // TODO withdraw

    // TODO add balance

}
