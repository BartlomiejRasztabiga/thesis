package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*

class UserNotFoundException(id: String) : NoSuchElementException("User with ID $id not found")
