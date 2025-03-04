package com.rickandmorty.rickandmortymultimodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rickandmorty.network.KtorClient
import com.rickandmorty.network.models.domain.Character
import com.rickandmorty.rickandmortymultimodule.component.CharacterDetailsScreen
import com.rickandmorty.rickandmortymultimodule.component.CharacterEpisodeScreen
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAndMortyMultiModuleTheme
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var character by remember {
                mutableStateOf<Character?>(null)
            }

            val scope = rememberCoroutineScope()

            LaunchedEffect (Unit){
                //character = ktorClient.getCharacters(1)
            }

            val navController = rememberNavController()

            RickAndMortyMultiModuleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "character_details"
                    ){
                        composable("character_details"){
                            CharacterDetailsScreen(
                                ktorClient = ktorClient,
                                onEpisodeClicked = {
                                    navController.navigate("character_episodes/$it")
                                },
                                onBackClicked = {

                                },
                                characterId = 114,
                            )
                        }
                        composable(
                            route = "character_episodes/{characterId}",
                            arguments = listOf(
                                navArgument("characterId"){
                                    type = NavType.IntType
                                }
                            )
                         ){ backStackEntry ->
                            CharacterEpisodeScreen(
                                characterId = backStackEntry.arguments?.getInt("characterId") ?: 0,
                                ktorClient = ktorClient
                            )
                        }
                    }
                }
            }
        }
    }
}