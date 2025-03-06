package com.rickandmorty.rickandmortymultimodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rickandmorty.network.KtorClient
import com.rickandmorty.rickandmortymultimodule.screen.detail_screen.CharacterDetailsScreen
import com.rickandmorty.rickandmortymultimodule.screen.episode_screen.CharacterEpisodeScreen
import com.rickandmorty.rickandmortymultimodule.screen.home_screen.HomeScreen
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAndMortyMultiModuleTheme
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient : KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            RickAndMortyMultiModuleTheme {

                    Scaffold(modifier = Modifier.fillMaxSize(),containerColor = RickPrimary) { innerPadding ->
                        NavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            startDestination = "home_screen"
                        ){
                            composable("home_screen"){
                                HomeScreen(
                                    onCharacterSelected = { characterId ->
                                        navController.navigate("character_details/$characterId")
                                    }
                                )
                            }
                            composable(
                                route = "character_details/{characterId}",
                                arguments = listOf(
                                    navArgument("characterId"){
                                        type = NavType.IntType
                                    }
                                )
                            ){ backStackEntry ->
                                CharacterDetailsScreen(
                                    characterId = backStackEntry.arguments?.getInt("characterId") ?: 0,
                                    onEpisodeClicked = {
                                        navController.navigate("character_episodes/$it")
                                    },
                                    onBackClicked = {
                                        navController.navigateUp()
                                    },
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
                                    ktorClient = ktorClient,
                                    onBackAction = {
                                        navController.navigateUp()
                                    }
                                )
                            }
                        }
                }
            }
        }
    }
}