package com.example.di

import com.mongodb.client.MongoClient
import org.koin.dsl.module
import org.litote.kmongo.KMongo

val mainModule = module(createdAtStart = true) {
    factory<MongoClient> { KMongo.createClient("mongodb://root:example@localhost:27017/?authSource=admin") }
}