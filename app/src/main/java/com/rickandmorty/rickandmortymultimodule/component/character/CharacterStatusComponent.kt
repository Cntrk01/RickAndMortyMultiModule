package com.rickandmorty.rickandmortymultimodule.component.character

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAndMortyMultiModuleTheme
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickTextPrimary

@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = characterStatus.toNewColor(),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 4.dp,
            )
    ) {
        Text(
            text = "Status: ${characterStatus.displayName}",
            fontSize = 20.sp,
            color = RickTextPrimary
        )
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewAlive() {
    RickAndMortyMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Alive)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewDead() {
    RickAndMortyMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Dead)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewUnknown() {
    RickAndMortyMultiModuleTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Unknown)
    }
}