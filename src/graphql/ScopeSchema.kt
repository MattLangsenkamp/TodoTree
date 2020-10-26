package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.model.LoggedInUser
import com.example.model.Scope
import com.example.services.ScopeService
import java.time.Instant

fun SchemaBuilder.scopeSchema(scopeService: ScopeService) {
    type<Scope>()
    query("scope") {
        resolver { id: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            scopeService.getScope(id)
        }
    }
    query("scopes") {
        resolver { userId: String,
                   ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            scopeService.getAllScopes(userId)
        }
    }
    mutation("addScope") {
        resolver { userId: String,
                   defaultScope: Boolean,
                   name: String,
                   description: String?,
                   startTime: Long?,
                   endTime: Long?, ctx: Context

            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            scopeService.addScope(userId, defaultScope, name, description, startTime, endTime)
        }
    }
    mutation("updateScope") {
        resolver { id: String,
                   userId: String?,
                   defaultScope: Boolean?,
                   name: String?,
                   description: String?,
                   startTime: Long?,
                   endTime: Long?, ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            scopeService.updateScope(id, userId, defaultScope, name, description, startTime, endTime)
        }
    }
    mutation("deleteScope") {
        resolver { id: String, ctx: Context
            ->
            val user: LoggedInUser = ctx.get<LoggedInUser>() ?: error("Not Logged In")
            scopeService.deleteScope(id)
        }
    }

}