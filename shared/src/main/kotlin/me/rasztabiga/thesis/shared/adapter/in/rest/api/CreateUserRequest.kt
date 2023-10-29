@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

data class CreateUserRequest(
    val name: String,
    val email: String
)
