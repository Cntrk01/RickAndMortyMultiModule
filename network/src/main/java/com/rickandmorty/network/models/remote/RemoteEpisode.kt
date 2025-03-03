package com.rickandmorty.network.models.remote

import com.rickandmorty.network.models.domain.Episode
import kotlinx.serialization.Serializable

@Serializable
data class RemoteEpisode(
    val id: Int,
    val name: String,
    val episode: String,
    val air_date: String,
    val characters: List<String>
)

fun RemoteEpisode.toDomainEpisode(): Episode {
    return Episode(
        id = id,
        name = name, //S03E07
        seasonNumber = episode.filter { it.isDigit() }.take(2).toInt(), //03
        episodeNumber = episode.filter { it.isDigit() }.takeLast(2).toInt(), //07
        airDate = air_date,
        characterIdsInEpisode = characters.map {
            it.substring(startIndex = it.lastIndexOf("/") + 1).toInt()
        }
    )
}
