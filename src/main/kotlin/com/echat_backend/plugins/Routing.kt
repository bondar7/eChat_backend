package com.echat_backend.plugins

import com.echat_backend.chat_sessions.SessionManager
import com.echat_backend.data.data_sources.messageDT.MessageDataSource
import com.echat_backend.data.data_sources.sessionDT.SessionDataSource
import com.echat_backend.data.data_sources.userDT.UserDataSource
import com.echat_backend.routes.*
import com.echat_backend.security.hashing.HashingService
import com.echat_backend.security.token.TokenConfig
import com.echat_backend.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting(
    ) {
    routing {
        val hashingService: HashingService by inject(HashingService::class.java)
        val userDataSource: UserDataSource by inject(UserDataSource::class.java)
        val sessionDataSource: SessionDataSource by inject(SessionDataSource::class.java)
        val sessionManager: SessionManager by inject(SessionManager::class.java)
        val messageDataSource: MessageDataSource by inject(MessageDataSource::class.java)
        val tokenService: TokenService by inject(TokenService::class.java)
        val tokenConfig: TokenConfig by inject(TokenConfig::class.java)

        // auth
        signUp(hashingService, userDataSource)
        logIn(userDataSource, hashingService, tokenService, tokenConfig)
        changeAvatar(userDataSource)
        checkUsername(userDataSource)
        changeUsername(userDataSource)
        changeName(userDataSource)
        changeEmail(userDataSource)
        checkEmail(userDataSource)
        changePassword(userDataSource)
        checkPassword(userDataSource)
        changeUserBio(userDataSource)
        authenticate()
        getSecretInfo()
        getUsersByUsername(userDataSource)

        // session
        createSession(sessionDataSource)

        // web socket
        chatSocket(
            sessionManager,
            messageDataSource,
            userDataSource
        )

        getMessagesBySessionId(sessionManager)

        getSessionsByUserId(
            sessionDataSource, userDataSource, messageDataSource
        )
    }
}