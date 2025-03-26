package com.rickandmortymultimodule.data.mappers

import com.rickandmortymultimodule.data.model.RemoteCharacter
import com.rickandmortymultimodule.data.model.RemoteCharacterPage
import com.rickandmortymultimodule.data.model.RemoteEpisode
import com.rickandmortymultimodule.data.model.RemoteEpisodePage
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.CharacterGender
import com.rickandmortymultimodule.domain.model.CharacterPage
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmortymultimodule.domain.model.Episode
import com.rickandmortymultimodule.domain.model.EpisodePage

fun RemoteCharacter.toDomainCharacter() : Character {
    val characterGender = when(gender.lowercase()){
        "male" -> CharacterGender.Male
        "female" -> CharacterGender.Female
        "genderless" -> CharacterGender.Genderless
        else -> CharacterGender.Unknown
    }

    val characterStatus = when(status.lowercase()){
        "alive" -> CharacterStatus.Alive
        "dead" -> CharacterStatus.Dead
        else -> CharacterStatus.Unknown
    }

    return Character(
        created = created,
        episodeIds = episode.map {
            it.substring(it.lastIndexOf("/") + 1).toInt()
        },//"https://rickandmortyapi.com/api/character/42" en sondaki / dan +1 sonrasını alarak id yi alabiliriz.
        gender = characterGender,
        id = id,
        imageUrl = image,
        location = Character.Location(
            name = location.name,
            url = location.url
        ),
        name = name,
        origin = Character.Origin(
            name = origin.name,
            url = origin.url
        ),
        species = species,
        status = characterStatus,
        type = type,
    )
}

fun RemoteCharacterPage.toDomainCharacterPage(): CharacterPage {
    return CharacterPage(
        info = CharacterPage.Info(
            count = info.count,
            pages = info.pages,
            next = info.next,
            prev = info.next
        ),
        characters = results.map {
            it.toDomainCharacter()
        }
    )
}


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

fun List<RemoteEpisode>.toDomainEpisodes(): List<Episode> {
    return this.map { it.toDomainEpisode() }
}

fun RemoteEpisodePage.toDomainEpisodePage(): EpisodePage {
    return EpisodePage(
        info = EpisodePage.Info(
            count = info.count,
            pages = info.pages,
            next = info.next,
            prev = info.prev
        ),
        episodes = results.map { it.toDomainEpisode() }
    )
}