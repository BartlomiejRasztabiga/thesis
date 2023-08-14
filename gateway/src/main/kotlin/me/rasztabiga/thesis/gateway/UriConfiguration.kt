package me.rasztabiga.thesis.gateway

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "uri")
data class UriConfiguration(
    val restaurant: String,
    val order: String,
    val payment: String,
    val delivery: String
)
