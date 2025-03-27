package com.rickandmortymultimodule.domain.model

//RemoteCharacterden bana character string olarak geliyor bende bu classa dönüşümünü sağlayarak kullanıyorum.
sealed class CharacterGender(val displayName : String){
    data object Male : CharacterGender("Male")
    data object Female : CharacterGender("Female")
    data object Unknown : CharacterGender("Unknown")
    data object Genderless : CharacterGender("Genderless")
}