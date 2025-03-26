package com.rickandmortymultimodule.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteEpisode(
    val id: Int,
    val name: String,
    val episode: String,
    val air_date: String,
    val characters: List<String>
)