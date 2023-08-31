package me.rasztabiga.thesis.query.domain.query.exception

import java.util.*
import kotlin.NoSuchElementException

class PayeeNotFoundException(id: UUID) : NoSuchElementException("Payee with ID $id not found")
