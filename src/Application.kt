package com.example

import com.apurebase.kgraphql.GraphQL
import com.example.di.mainModule
import com.example.graphql.authSchema
import com.example.graphql.scopeSchema
import com.example.graphql.todoSchema
import com.example.services.AuthService
import com.example.services.ScopeService
import com.example.services.TodoService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger

fun main(args: Array<String>) :Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
//@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    startKoin {
        PrintLogger()
        modules(mainModule)
    }
    install(GraphQL) {
        val authService = AuthService()
        val todoService = TodoService()
        val scopeService = ScopeService(todoService)

        context { call ->
            authService.verifyToken(call)?.let {
                +it
            }
            +log
            +call
        }
        playground = true

        schema {
            authSchema(authService)
            todoSchema(todoService)
            scopeSchema(scopeService)
        }
    }
}

