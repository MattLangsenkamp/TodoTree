package com.example.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.model.User
import com.example.services.AuthService
import io.ktor.application.*
import org.slf4j.Logger
import com.apurebase.kgraphql.Context

fun SchemaBuilder.authSchema(authService: AuthService) {
    type<User>()
    mutation("signIn") {
        resolver { email: String,
                   password: String,
                    ctx: Context
            ->
            val call = ctx.get<ApplicationCall>()!!
            val log = ctx.get<Logger>()!!
            authService.signIn(call = call, email = email, password = password)
        }
    }

    mutation("signUp") {
        resolver { email: String,
                   password: String,
                   ctx: Context
            ->
            val call = ctx.get<ApplicationCall>()!!
            val log = ctx.get<Logger>()!!
            authService.signUp(call=call, email = email, password = password, permissionLevel = "User")
        }
    }
}