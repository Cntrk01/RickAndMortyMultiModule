package com.rickandmorty.rickandmortymultimodule.component.character

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rickandmortymultimodule.domain.model.Character
import com.rickandmortymultimodule.domain.model.CharacterGender
import com.rickandmortymultimodule.domain.model.CharacterStatus
import com.rickandmortymultimodule.common.ui_elements.CharacterImage
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction

@Composable
fun CharacterGridItem(
    modifier: Modifier = Modifier,
    character: Character,
    onClick: () -> Unit,
) {
    Column (
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, RickAction)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
    ){
        Box {
            CharacterImage(
                modifier = Modifier.fillMaxWidth(),
                imageUrl = character.imageUrl,
            )
            CharacterStatusCircle(
                status = character.status,
                modifier = Modifier.padding(start = 6.dp, top = 6.dp)
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            text = character.name,
            color = RickAction,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 26.sp
        )
    }
}


@Preview
@Composable
fun CharacterGridItemPreview(){
    CharacterGridItem(
        modifier = Modifier.fillMaxWidth(),
        character = Character(
            created = "timestamp",
            episodeIds = listOf(1, 2, 3, 4, 5),
            gender = CharacterGender.Male,
            id = 123,
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            location = Character.Location(
                name = "Earth",
                url = ""
            ),
            name = "Morty Smith",
            origin = Character.Origin(
                name = "Earth",
                url = ""
            ),
            species = "Human",
            status = CharacterStatus.Alive,
            type = ""
        ),
        onClick = {}
    )
}