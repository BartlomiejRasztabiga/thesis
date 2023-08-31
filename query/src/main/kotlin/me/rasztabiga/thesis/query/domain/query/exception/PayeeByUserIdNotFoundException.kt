package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*
import kotlin.NoSuchElementException

class PayeeByUserIdNotFoundException(userId: String) : NoSuchElementException("Payee with userId $userId not found")
