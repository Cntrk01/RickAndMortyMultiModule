package com.rickandmorty.rickandmortymultimodule.component.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmortymultimodule.common.ui_elements.CharacterNameComponent

@Composable
fun CharacterDetailsNamePlateComponent(
    modifier: Modifier = Modifier,
    name: String,
    status: CharacterStatus
) {
    Column(modifier = modifier.fillMaxWidth()) {
        CharacterStatusComponent(characterStatus = status)
        CharacterNameComponent(name = name)
    }
}

@Preview
@Composable
fun NamePlatePreviewAlive() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Alive)
}

@Preview
@Composable
fun NamePlatePreviewDead() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Dead)
}

@Preview
@Composable
fun NamePlatePreviewUnknown() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Unknown)
}