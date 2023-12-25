package com.echat_backend.plugins

import com.echat_backend.*
import com.echat_backend.data.data_sources.UserDataSource
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.hashing.SHA256HashingService
import com.echat_backend.security.token.TokenConfig
import com.echat_backend.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting(
    ) {
    routing {
        val hashingService: HashingService by inject(HashingService::class.java)
        val userDataSource: UserDataSource by inject(UserDataSource::class.java)
        val tokenService: TokenService by inject(TokenService::class.java)
        val tokenConfig: TokenConfig by inject(TokenConfig::class.java)

        signUp(hashingService, userDataSource)
        logIn(userDataSource, hashingService, tokenService, tokenConfig)
        checkUsername(userDataSource)
        changeUsername(userDataSource)
        changeEmail(userDataSource)
        changePassword(userDataSource)
        checkPassword(userDataSource)
        changeUserBio(userDataSource)
        authenticate()
        getSecretInfo()
    }
}
