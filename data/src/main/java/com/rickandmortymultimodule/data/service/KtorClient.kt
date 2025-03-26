package com.rickandmortymultimodule.data.service

import com.rickandmortymultimodule.common.ApiOperation
import com.rickandmortymultimodule.data.model.RemoteCharacter
import com.rickandmortymultimodule.data.model.RemoteCharacterPage
import com.rickandmortymultimodule.data.model.RemoteEpisode
import com.rickandmortymultimodule.data.model.RemoteEpisodePage
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

    private suspend fun getCharacterByPage(
        pageNumber: Int,
        queryParams: Map<String, String>
    ): ApiOperation<RemoteCharacterPage> {
        return safeApiCall {
            client.get("character") {
                url {
                    parameters.append("page", pageNumber.toString())
                    queryParams.forEach { parameters.append(it.key, it.value) }
                }
            }
                .body<RemoteCharacterPage>()
        }
    }

    suspend fun searchAllCharactersByName(
        searchQuery: String
    ): ApiOperation<List<RemoteCharacter>> {
        return getCharacterByPage(
            pageNumber = 1,
            queryParams = mapOf("name" to searchQuery)
        ).operationFlatMap { firstPage ->
            val totalPageCount = firstPage.info.pages

            (2..totalPageCount).fold(
                initial = ApiOperation.Success(firstPage.results.toMutableList()) as ApiOperation<MutableList<RemoteCharacter>>
            ) { acc, pageIndex ->
                acc.operationFlatMap { characters ->
                    getCharacterByPage(
                        pageNumber = pageIndex,
                        queryParams = mapOf("name" to searchQuery)
                    ).mapSuccess { nextPage ->
                        characters.addAll(nextPage.results)
                        characters
                    }
                }
            }.mapSuccess { it.toList() } // Listeye dönüştürme
        }
    }

    private suspend fun getEpisodesByPage(pageIndex : Int) : ApiOperation<RemoteEpisodePage> {
        return safeApiCall {
            client.get("episode"){
                url{
                    parameters.append("page",pageIndex.toString())
                }
            }.body<RemoteEpisodePage>()
        }
    }

    suspend fun getAllEpisodes(): ApiOperation<List<RemoteEpisode>> {
        return getEpisodesByPage(1)
            .operationFlatMap { firstPageResult ->
                val totalPageCount = firstPageResult.info.pages
                (2..totalPageCount).fold(
                    initial = ApiOperation.Success(firstPageResult.results.toMutableList()) as ApiOperation<MutableList<RemoteEpisode>>
                ) { acc, pageIndex ->
                    acc.operationFlatMap { episodes ->
                        getEpisodesByPage(pageIndex)
                            .mapSuccess { nextPage ->
                                episodes.addAll(nextPage.results)
                                episodes
                        }
                    }
                }
            .mapSuccess {
                it.toList()
            }
        }
    }

    private var characterCache = mutableMapOf<Int, RemoteCharacter>()

    //client.get("character/$id"): GET isteği yapar
    //.body<Character>(): Yanıtı Character veri modeline dönüştürür.
    suspend fun getCharacters(id : Int) : ApiOperation<RemoteCharacter> {
        characterCache[id]?.let { return ApiOperation.Success(data = it) } //Cache olayı yaptık.Burda aşağıda eklediği alsodan sonra kontrol yapıyor eğer bu idye sahipse api çağırısı yapmadan bloktan çıkıyor.

        return safeApiCall {
            client
                .get("character/$id")
                .body<RemoteCharacter>()
                .also {
                    characterCache[id] = it
                }
        }
    }

    suspend fun getCharacterByPage(pageNumber : Int) : ApiOperation<RemoteCharacterPage> {
        return safeApiCall {
            client
                .get("character/?page=$pageNumber")
                .body<RemoteCharacterPage>()
        }
    }

    //şimdi şöyle 114.id de episode[] json değişkeninde yanlızca 1 tane episode olduğu için 1 taneyi liste olarak setlemeye çalıştığımızda(.get("episode/$idsSeparated") kısımında) episode gelmiyordu.
    //bundan dolayı 1 tane elemanı olan episode[] leri tek değerle apiden almamız gerekli.Ondan dolayı burda böylke bir method ekledim.
    private suspend fun getEpisodes(episodeId : Int) : ApiOperation<RemoteEpisode> {
        return safeApiCall {
            client
                .get("episode/$episodeId")
                .body<RemoteEpisode>()
        }
    }

    //mapSuccess deki R çıkış tipi aslıdna burdaki methodun dönüş tipini baz alır.Ve gelen T tipiyle sen burdaki R tipine dönüştürmeni bekler.
    //getEpisodes deki T type Episode .Ben mapSuccess içinde bunu listOf yaparak aslında dıştaki methodun tipinde döndürdüm yani R tipi.
    suspend fun getEpisodes(episodesIds : List<Int>) : ApiOperation<List<RemoteEpisode>> {
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
            }
        }
    }

    private inline fun <T>safeApiCall(apiCall : ()->T) : ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        }catch (e : Exception){
            ApiOperation.Failure(exception = e)
        }
    }
}