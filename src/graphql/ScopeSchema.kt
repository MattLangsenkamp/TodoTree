package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.customExceptions.NotLoggedInExceptionException
import com.example.model.LoggedInUser
import com.example.model.Scope
import com.example.services.ScopeService
import com.example.customExceptions.catchExceptions
import org.slf4j.Logger



fun SchemaBuilder.scopeSchema(scopeService: ScopeService) {

    type<Scope>()
    query("scope") {
        resolver { id: String,
                   ctx: Context
            ->
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                scopeService.getScope(user, id)
            }
            log.info(result.toString())
            result.first
        }
    }
    query("scopes") {
        resolver { ctx: Context
            ->
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                scopeService.getAllScopes(user)
            }
            log.info(result.toString())
            result.first
        }
    }
    mutation("addScope") {
        resolver { defaultScope: Boolean,
                   name: String,
                   description: String?,
                   startTime: Long?,
                   endTime: Long?, ctx: Context

            ->
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                scopeService.addScope(user, defaultScope, name, description, startTime, endTime)
            }
            log.info(result.toString())
            result.first
        }
    }
    mutation("updateScope") {
        resolver { id: String,
                   defaultScope: Boolean?,
                   name: String?,
                   description: String?,
                   startTime: Long?,
                   endTime: Long?, ctx: Context
            ->
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                scopeService.updateScope(user, id, defaultScope, name, description, startTime, endTime)
            }
            log.info(result.toString())
            result.first
        }
    }
    mutation("deleteScope") {
        resolver { id: String,
                   ctx: Context
            ->
            val log = ctx.get<Logger>()!!
            val result = catchExceptions {
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                scopeService.deleteScope(user, id)
            }
            log.info(result.toString())
            result.first
        }
    }
}