package com.rickandmorty.network

import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.Episode
import com.rickandmorty.network.models.remote.RemoteCharacter
import com.rickandmorty.network.models.remote.RemoteEpisode
import com.rickandmorty.network.models.remote.toDomainCharacter
import com.rickandmorty.network.models.remote.toDomainEpisode
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
import java.lang.Exception

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
    private var characterCache = mutableMapOf<Int,Character>()

    //client.get("character/$id"): GET isteği yapar
    //.body<Character>(): Yanıtı Character veri modeline dönüştürür.
    suspend fun getCharacters(id : Int) : ApiOperation<Character>  {
        characterCache[id]?.let { return ApiOperation.Success(data = it) } //Cache olayı yaptık.Burda aşağıda eklediği alsodan sonra kontrol yapıyor eğer bu idye sahipse api çağırısı yapmadan bloktan çıkıyor.

        return safeApiCall {
            client
                .get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also {
                    characterCache[id] = it
                }
        }
    }

    suspend fun getEpisodes(episodesIds : List<Int>) : ApiOperation<List<Episode>>{
        val idsSeparated = episodesIds.joinToString(separator = ",")
        return safeApiCall {
            client
                .get("episode/$idsSeparated") //gelen idler joinToString ile stringe çevirilir ör : "1,2,3" şeklinde istek atılıp datayı çekeriz.
                .body<List<RemoteEpisode>>()
                .map { it.toDomainEpisode() }
        }
    }

    private inline fun <T>safeApiCall(apiCall : ()->T) : ApiOperation<T>{
        return try {
            ApiOperation.Success(data = apiCall())
        }catch (e : Exception){
            ApiOperation.Failure(exception = e)
        }
    }
}

sealed interface ApiOperation<T>{
    data class Success<T>(val data : T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun onSuccess(callback : (T) -> Unit) : ApiOperation<T> {
        if (this is Success) callback(data)
        return this
    }

    fun onFailure(callback : (Exception) -> Unit) : ApiOperation<T> {
        if (this is Failure) callback(exception)
        return this
    }

}