package com.example

import com.example.di.mainModule
import com.example.model.Todo
import com.example.repository.TodoRepository
import com.example.services.TodoService
import com.mongodb.client.model.Aggregates.graphLookup
import com.mongodb.client.model.GraphLookupOptions
import org.koin.core.context.startKoin
import org.litote.kmongo.*
import java.lang.reflect.TypeVariable
import java.time.Instant

fun main() {

    startKoin {
        modules(mainModule)
    }

    val mongoClient = KMongo.createClient("mongodb://root:example@localhost:27017/?authSource=admin")
    val repo = TodoRepository(mongoClient)

    val service = TodoService()
    val td4 = Todo(
        id = "23477",
        userId = "sr pee pee22222222222222222",
        creationTimeStamp = Instant.now(),
        text = "poop",
        completed = true,
        scopeId = "yea",
        rootTodo = false,
        parentTodoId = "2394",
        children = listOf()

    )
    val td1 = Todo(
        id = "2394",
        userId = "sr pee pee22222222222222222",
        creationTimeStamp = Instant.now(),
        text = "poop",
        completed = true,
        scopeId = "yea",
        rootTodo = false,
        parentTodoId = "2348",
        children = listOf("23477")

    )

    val td2 = Todo(
        id = "2348",
        userId = "sr pee pee3333333333",
        creationTimeStamp = Instant.now(),
        text = "poop",
        completed = true,
        scopeId = "yea",
        rootTodo = false,
        parentTodoId = "234",
        children = listOf("2394")

    )
    val td = Todo(
        id = "234",
        userId = "sr pee pee",
        creationTimeStamp = Instant.now(),
        text = "poop",
        completed = true,
        scopeId = "yea",        rootTodo = true,
        parentTodoId = null,
        children = listOf("2348")

    )
    repo.add(td)
    repo.add(td1)
    repo.add(td2)
    repo.add(td4)

    //service.addTodo(td.userId,td.text, td.completed, td.scopeId, children = td.children, parentTodoId = null, rootTodo = true)

    //val poo= mutableListOf(Todo::id)
    //val col = repo.col

}

