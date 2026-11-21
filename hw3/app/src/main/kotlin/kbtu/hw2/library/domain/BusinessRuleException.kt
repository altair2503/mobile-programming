package kbtu.hw2.library.domain

/** Thrown when an operation violates a business rule; the message is shown to the user. */
class BusinessRuleException(message: String) : Exception(message)
