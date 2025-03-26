package com.rickandmorty.rickandmortymultimodule.component.character

import androidx.compose.ui.graphics.Color
import com.rickandmortymultimodule.domain.model.CharacterStatus

fun CharacterStatus.toNewColor(): Color {
    return when (this) {
        CharacterStatus.Alive -> Color.Green
        CharacterStatus.Dead -> Color.Red
        CharacterStatus.Unknown -> Color.Yellow
    }
}