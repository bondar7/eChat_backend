package com.echat_backend.di

import com.echat_backend.data.data_sources.MongoUserDataSource
import com.echat_backend.data.data_sources.UserDataSource
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.hashing.SHA256HashingService
import com.echat_backend.security.token.JwtTokenService
import com.echat_backend.security.token.TokenConfig
import com.echat_backend.security.token.TokenService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    val mongoPw = System.getenv("MONGO_PW")
    val mongoDbName = "echat_backend"

    single {
        KMongo.createClient(
            connectionString =
            "mongodb+srv://maksimbondar:$mongoPw@cluster0.gjmxfwx.mongodb.net/$mongoDbName?retryWrites=true&w=majority"
        ).coroutine.getDatabase(mongoDbName)
    }

    single<UserDataSource> {
        MongoUserDataSource(get(), get())
    }

    single<TokenService> {
        JwtTokenService()
    }
    single<HashingService> {
        SHA256HashingService()
    }
    single<TokenConfig> {
        val issuer = "http://0.0.0.0:8080"
        val audience = "users"
        TokenConfig(
            issuer = issuer,
            audience = audience,
            expireIn = 365L * 1000L * 60L * 60L * 24L, // 1 рік
            secret = System.getenv("JWT_SECRET")
        )
    }
}