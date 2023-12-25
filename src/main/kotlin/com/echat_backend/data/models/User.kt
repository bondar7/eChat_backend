package com.echat_backend.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val email: String,
    val password: String,
    val userBio: String = "Bio",
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
)