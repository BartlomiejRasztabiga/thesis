package me.rasztabiga.thesis.order.domain.query.exception

import java.util.*

class UserNotFoundException(id: String) : NoSuchElementException("User with ID $id not found")
