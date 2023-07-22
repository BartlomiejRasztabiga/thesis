package me.rasztabiga.thesis.shared.config

import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getUserId(): String {
    return getAttribute<String>(USER_ID) ?: error("User id not found")
}

fun ServerWebExchange.setUserId(userId: String) {
    attributes[USER_ID] = userId
}

const val USER_ID = "userId"
