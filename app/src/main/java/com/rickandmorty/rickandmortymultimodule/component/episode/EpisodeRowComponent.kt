package com.rickandmorty.rickandmortymultimodule.component.episode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rickandmortymultimodule.common.ui_elements.DataPoint
import com.rickandmortymultimodule.common.ui_elements.DataPointComponent
import com.rickandmortymultimodule.domain.model.Episode
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickTextPrimary

@Composable
fun EpisodeRowComponent(episode: Episode) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DataPointComponent(
            dataPoint = DataPoint(
                title = "Episode",
                description = episode.episodeNumber.toString()
            )
        )
        Spacer(modifier = Modifier.width(64.dp))
        Column {
            Text(
                text = episode.name,
                fontSize = 24.sp,
                color = RickTextPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = episode.airDate,
                fontSize = 16.sp,
                color = RickTextPrimary,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun EpisodeRowComponentPreview() {
    val episode = Episode(
        id = 28,
        name = "The Ricklantis Mixup",
        seasonNumber = 3,
        episodeNumber = 7,
        airDate = "September 10, 2017",
        characterIdsInEpisode = listOf(
            1,
            2,
            4,
            8,
            18,
            22,
            27,
            43,
            44,
            48,
            56,
            61,
            72,
            73,
            74,
            78,
            85,
            86,
            118,
            123,
            135,
            143,
        )
    )

    EpisodeRowComponent(episode = episode)
}