package me.rasztabiga.thesis.shared.security

object Scopes {

    val RESTAURANT = RestaurantScopes

    object RestaurantScopes {
        const val READ = "SCOPE_restaurant.read"
    }
}
