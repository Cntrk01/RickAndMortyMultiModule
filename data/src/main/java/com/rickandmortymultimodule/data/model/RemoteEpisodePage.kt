package com.rickandmortymultimodule.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteEpisodePage(
    val info: Info,
    val results: List<RemoteEpisode>
) {
    @Serializable
    data class Info(
        val count: Int,
        val pages: Int,
        val next: String?,
        val prev: String?
    )
}