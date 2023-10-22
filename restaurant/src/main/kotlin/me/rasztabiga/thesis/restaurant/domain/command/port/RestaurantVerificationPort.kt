package me.rasztabiga.thesis.restaurant.domain.command.port

interface RestaurantVerificationPort {

    fun verifyRestaurantByManagerIdExists(managerId: String): Boolean
}
