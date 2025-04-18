package com.rickandmorty.rickandmortymultimodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rickandmorty.rickandmortymultimodule.screen.all_episode.AllEpisodeScreen
import com.rickandmorty.rickandmortymultimodule.screen.detail.CharacterDetailsScreen
import com.rickandmorty.rickandmortymultimodule.screen.detail.detail_to_episode.CharacterEpisodeScreen
import com.rickandmorty.rickandmortymultimodule.screen.home.HomeScreen
import com.rickandmorty.rickandmortymultimodule.screen.search.SearchScreen
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAction
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickAndMortyMultiModuleTheme
import com.rickandmorty.rickandmortymultimodule.ui.theme.RickPrimary
import dagger.hilt.android.AndroidEntryPoint

sealed class NavDestination(
    val title : String,
    val route: String,
    val icon : ImageVector,
) {
    data object Home : NavDestination(title = "Home", route = "home_screen", icon = Icons.Default.Home)
    data object Episodes : NavDestination(title = "Episodes", route = "episodes", icon = Icons.Default.PlayArrow)
    data object Search : NavDestination(title = "Search", route = "search", icon = Icons.Default.Search)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            val bottomBarItems = listOf(
                NavDestination.Home,
                NavDestination.Episodes,
                NavDestination.Search
            )

            var selectedIndex by remember {
                mutableIntStateOf(0)
            }

            RickAndMortyMultiModuleTheme {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        containerColor = RickPrimary,
                        bottomBar = {
                            NavigationBar(
                                containerColor = RickPrimary,
                            ) {
                                bottomBarItems.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(imageVector = item.icon, contentDescription = item.title)
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        selected = index == selectedIndex,
                                        onClick = {
                                            selectedIndex = index // bu kodla birlikte mesela home sayfasında bir iteme tıklayınca farklı sayfaya gidince bottomnavın rengi kayıp oluyor
                                            //home seçili ya zaten o selectedColor gidiyor.Burda onu düzelttik.// currentDestination?.hierarchy?.any { it.route == item.route } == true
                                            navController.navigate(item.route){
                                                popUpTo(navController.graph.startDestinationId){
                                                    saveState = true
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = RickAction,
                                            selectedTextColor = RickAction,
                                            indicatorColor = Color.Transparent,
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            startDestination = "home_graph",
                        ) {
                            //Bunlar birer farklı navigation graph olduğu için sadece ilk itemin yani Home.route statesini koruyor.Diğerlerini hep en baştan başlatıyor.Fakat bunun da sebebi  popUpTo(navController.graph.startDestinationId) şu kısımla ilgili olduğunu düşünüyorum.Çözümünü bulamadım.
                            //Ama bunları direk navigation olmadan navhost içerisinde composable olarak çağırdığımızda her bottomnavitemin statesini tututyor ve tıkladığımda ordan devam ediyor.
                            navigation(startDestination = NavDestination.Home.route, route = "home_graph") {
                                composable("home_screen") {
                                    HomeScreen(
                                        onCharacterSelected = { characterId ->
                                            navController.navigate("character_details/$characterId")
                                        }
                                    )
                                }
                                composable(
                                    route = "character_details/{characterId}",
                                    arguments = listOf(
                                        navArgument("characterId") {
                                            type = NavType.IntType
                                        }
                                    )
                                ) { backStackEntry ->
                                    CharacterDetailsScreen(
                                        characterId = backStackEntry.arguments?.getInt("characterId")
                                            ?: 0,
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
                                        navArgument("characterId") {
                                            type = NavType.IntType
                                        }
                                    )
                                ) { backStackEntry ->
                                    CharacterEpisodeScreen(
                                        characterId = backStackEntry.arguments?.getInt("characterId")
                                            ?: 0,
                                        onBackAction = {
                                            navController.navigateUp()
                                        }
                                    )
                                }
                            }

                            navigation(startDestination = NavDestination.Episodes.route, route = "episodes_graph"){

                                composable(route = NavDestination.Episodes.route) {
                                    AllEpisodeScreen()
                                }
                            }

                            navigation(startDestination = NavDestination.Search.route, route = "search_graph"){
                                composable(route = NavDestination.Search.route) {
                                    SearchScreen(
                                        onCharacterClicked = { characterId ->
                                            navController.navigate("character_details/$characterId")
                                        }
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}