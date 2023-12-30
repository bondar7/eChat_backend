package com.echat_backend.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangeNameRequest(
    val usernameToFindUser: String,
    val newName: String
)