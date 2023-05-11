package com.group.libraryapp.util

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

fun fail(): Nothing {
    throw IllegalArgumentException()
}

fun <T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T {
    return this.findByIdOrNull(id) ?: fail()
}

fun UserRepository.findByNameOrThrow(name: String): User {
    return this.findByName(name) ?: fail()
}