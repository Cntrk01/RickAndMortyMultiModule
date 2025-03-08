package com.rickandmorty.network

import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.network.models.domain.CharacterPage
import com.rickandmorty.network.models.domain.Episode
import com.rickandmorty.network.models.domain.EpisodePage
import com.rickandmorty.network.models.remote.RemoteCharacter
import com.rickandmorty.network.models.remote.RemoteCharacterPage
import com.rickandmorty.network.models.remote.RemoteEpisode
import com.rickandmorty.network.models.remote.RemoteEpisodePage
import com.rickandmorty.network.models.remote.toDomainCharacter
import com.rickandmorty.network.models.remote.toDomainCharacterPage
import com.rickandmorty.network.models.remote.toDomainEpisode
import com.rickandmorty.network.models.remote.toDomainEpisodePage
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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

        expectSuccess = true
    }

    private suspend fun getEpisodesByPage(pageIndex : Int) : ApiOperation<EpisodePage>{
        return safeApiCall {
            client.get("episode"){
                url{
                    parameters.append("page",pageIndex.toString())
                }
            }.body<RemoteEpisodePage>()
                .toDomainEpisodePage()
        }
    }

    suspend fun getAllEpisodes(): ApiOperation<List<Episode>> {
        val data = mutableListOf<Episode>()
        var exception : Exception ?= null
        var totalPageCount = 0

        try {
            val firstJob = CoroutineScope(Dispatchers.IO).launch {
                getEpisodesByPage(pageIndex = 1)
                    .onSuccess { firstPageResult ->
                        totalPageCount = firstPageResult.info.pages
                        data.addAll(firstPageResult.episodes.toMutableList())
                    }
                    .onFailure {
                        exception = it
                    }
            }

            firstJob.join()

            if (exception == null) {
                val secondJob = CoroutineScope(Dispatchers.IO).launch {
                    val deferred = (2..totalPageCount).map { pageIndex ->
                        async {
                            getEpisodesByPage(pageIndex)
                                .onSuccess { nextPage ->
                                    data.addAll(nextPage.episodes)
                                }
                                .onFailure {
                                    exception = it
                                }
                        }
                    }
                    deferred.awaitAll()
                }
                secondJob.join()
            }

            ApiOperation.Success(data.toList())

        } catch (e: Exception) {
            ApiOperation.Failure(e)
        }

        return exception?.let { ApiOperation.Failure(it) } ?: ApiOperation.Success(data.toList())
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
    suspend fun getCharacterByPage(pageNumber : Int) : ApiOperation<CharacterPage>{
        return safeApiCall {
            client
                .get("character/?page=$pageNumber")
                .body<RemoteCharacterPage>()
                .toDomainCharacterPage()
        }
    }

    //şimdi şöyle 114.id de episode[] json değişkeninde yanlızca 1 tane episode olduğu için 1 taneyi liste olarak setlemeye çalıştığımızda(.get("episode/$idsSeparated") kısımında) episode gelmiyordu.
    //bundan dolayı 1 tane elemanı olan episode[] leri tek değerle apiden almamız gerekli.Ondan dolayı burda böylke bir method ekledim.
    private suspend fun getEpisodes(episodeId : Int) : ApiOperation<Episode>{
        return safeApiCall {
            client
                .get("episode/$episodeId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }

    //mapSuccess deki R çıkış tipi aslıdna burdaki methodun dönüş tipini baz alır.Ve gelen T tipiyle sen burdaki R tipine dönüştürmeni bekler.
    //getEpisodes deki T type Episode .Ben mapSuccess içinde bunu listOf yaparak aslında dıştaki methodun tipinde döndürdüm yani R tipi.
    suspend fun getEpisodes(episodesIds : List<Int>) : ApiOperation<List<Episode>>{
        val idsSeparated = episodesIds.joinToString(separator = ",")

        return if (episodesIds.size == 1){
            getEpisodes(episodeId = episodesIds.first())
                .mapSuccess {
                    listOf(it)
                }
        }else{
            safeApiCall {
                client
                    .get("episode/$idsSeparated") //gelen idler joinToString ile stringe çevirilir ör : "1,2,3" şeklinde istek atılıp datayı çekeriz.
                    .body<List<RemoteEpisode>>()
                    .map { it.toDomainEpisode() }
            }
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

    fun <R> mapSuccess(callback : (T) -> R) : ApiOperation<R>{
        return when(this){
            is Success -> Success(data = callback(data))
            is Failure -> Failure(exception = exception)
        }
    }

    fun onSuccess(callback : (T) -> Unit) : ApiOperation<T> {
        if (this is Success) callback(data)
        return this
    }

    fun onFailure(callback : (Exception) -> Unit) : ApiOperation<T> {
        if (this is Failure) callback(exception)
        return this
    }

}