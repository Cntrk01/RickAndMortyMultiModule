package com.rickandmorty.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp){
        defaultRequest {
            url("https://rickandmortyapi.com/api/")
        }

        install(Logging) {
            logger = Logger.SIMPLE // Gelen/giden HTTP isteklerini loglamak için kullanılır
        }

        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true //API'den gelen ancak Character modelinde tanımlı olmayan alanlar göz ardı edilir ve hata alınmaz.
            })
        }
    }
    //client.get("character/$id"): GET isteği yapar
    //.body<Character>(): Yanıtı Character veri modeline dönüştürür.
    suspend fun getCharacters(id : Int) : Character = client.get("character/$id").body()
}

@Serializable
data class Character(
    val id : Int,
    val name : String,
    val origin : Origin,
){
    @Serializable
    data class Origin(
        val name : String,
    )
}