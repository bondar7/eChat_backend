package com.echat_backend

import com.echat_backend.di.mainModule
import com.echat_backend.plugins.*
import com.echat_backend.security.token.TokenConfig
import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent

fun main(args: Array<String>) {
    try {
        io.ktor.server.netty.EngineMain.main(args)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun Application.module() {
    startKoin {
        modules(mainModule)
    }
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
