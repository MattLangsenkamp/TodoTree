package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.customExceptions.ServerError
import com.example.customExceptions.catchExceptions
import com.example.model.EncodedTokens
import com.example.model.User
import com.example.services.AuthService
import io.ktor.application.*
import org.slf4j.Logger
import java.time.Instant

sealed class AuthUnion {
    data class EncodedTokens(
        val AccessToken: String?,
        val RefreshToken: String?): AuthUnion()

}

fun SchemaBuilder.authSchema(authService: AuthService) {
    stringScalar<Instant> {
        deserialize = { instant: String -> Instant.parse(instant) }
        serialize = Instant::toString
    }
    val unionExample = unionType("UnionExample"){
        type<EncodedTokens>()
        type<ServerError>()
    }
    type<User>()
    mutation("signIn") {
        val returnType = unionType("UnionExample2"){
        type<EncodedTokens>()
        type<ServerError>()
    }
        resolver { email: String,
                   password: String,
                   ctx: Context
            ->
            val call = ctx.get<ApplicationCall>()!!
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                authService.signIn(call = call, email = email, password = password)
            }
            log.info(result.toString())
            result.first
        }
    }

    mutation("signUp") {

        resolver { email: String,
                   password: String,
                   ctx: Context
            ->
            val call = ctx.get<ApplicationCall>()!!
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                authService.signUp(
                    call = call,
                    email = email,
                    password = password,
                    permissionLevel = "User"
                )
            }
            log.info(result.toString())
            result.first
        }
    }
}