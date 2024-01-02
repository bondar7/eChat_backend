package com.echat_backend.data.requests

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId


@Serializable
data class CreateSessionRequest(
    val user1Id: String,
    val user2Id: String,
)