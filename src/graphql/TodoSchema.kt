package com.example.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.example.customExceptions.NotLoggedInExceptionException
import com.example.customExceptions.catchExceptions
import com.example.model.LoggedInUser
import com.example.model.Todo
import com.example.services.TodoService

fun SchemaBuilder.todoSchema(todoService: TodoService) {

    type<Todo>()
    query("todo") {
        resolver { id: String,
                   ctx: Context
            ->
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                todoService.getTodo(user, id)
        }
    }

    query("todos") {
        resolver { scopeId: String?,
                   rootTodo: Boolean?,
                   ctx: Context
            ->
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                todoService.getAllTodos(user, scopeId, rootTodo)
        }
    }

    mutation("addTodo") {
        resolver { text: String,
                   completed: Boolean,
                   scopeId: String,
                   rootTodo: Boolean,
                   parentTodoId: String?,
                   ctx: Context
            ->
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                todoService.addTodo(user, text, completed, scopeId, rootTodo, parentTodoId)
        }
    }

    mutation("updateTodo") {
        resolver { id: String,
                   text: String?,
                   completed: Boolean?,
                   scopeId: String?,
                   children: List<String>?,
                   ctx: Context
            ->
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                todoService.updateTodo(user, id, text, completed, scopeId, children)
        }
    }

    mutation("deleteTodo") {
        resolver { id: String,
                   ctx: Context
            ->
                val user: LoggedInUser = ctx.get() ?: throw NotLoggedInExceptionException("Not Logged In")
                todoService.deleteTodo(user, id)
        }
    }
}