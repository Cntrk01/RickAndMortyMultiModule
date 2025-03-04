package com.rickandmorty.rickandmortymultimodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rickandmorty.network.KtorClient
import com.rickandmorty.rickandmortymultimodule.screen.detail_screen.CharacterDetailsScreen
import com.rickandmorty.rickandmortymultimodule.screen.CharacterEpisodeScreen
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home_screen"
                    ){
                        composable("home_screen"){
                            HomeScreen(
                                onCharacterSelected = {
                                    //navController.navigate("character_details")
                                }
                            )
                        }
                        composable("character_details"){
                            CharacterDetailsScreen(
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