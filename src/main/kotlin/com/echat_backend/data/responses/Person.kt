package com.echat_backend.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: String,
    val username: String,
    val name: String,
    val avatar: ByteArray?,
    val bio: String
)
