package com.example.graphql
import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.model.Scope
import com.example.services.ScopeService

fun SchemaBuilder.scopeSchema(scopeService: ScopeService) {
    type<Scope>()
    query("scope") {
        resolver {
            id: String,
            ctx: Context
            ->

        }
    }
    query("scopes") {
        resolver { userId: String
        ->

        }
    }
    mutation("addScope") {
        resolver {
            id: String ->
        }
    }
    mutation("updateScope") {
        resolver {
                id: String ->

        }
    }
    mutation("deleteScope") {
        resolver {
                id: String ->
        }
    }

}