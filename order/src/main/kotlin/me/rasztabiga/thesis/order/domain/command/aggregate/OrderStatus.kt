package me.rasztabiga.thesis.order.domain.command.aggregate

enum class OrderStatus {
    CREATED,
    CANCELED,
    FINALIZED,
    PAID,
    REJECTED,
    DELIVERED
}
