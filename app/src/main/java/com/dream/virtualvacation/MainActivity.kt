package com.dream.virtualvacation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dream.virtualvacation.data.model.City
import com.dream.virtualvacation.ui.screens.CityListScreen
import com.dream.virtualvacation.ui.screens.VideoPlayerScreen
import com.dream.virtualvacation.ui.theme.VirtualVacationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirtualVacationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VirtualVacationApp()
                }
            }
        }
    }
}

@Composable
fun VirtualVacationApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "city_list"
    ) {
        composable("city_list") {
            CityListScreen(
                onCityClick = { city ->
                    navController.navigate("video_player/${city.name}/${city.videoId}")
                }
            )
        }
        
        composable("video_player/{cityName}/{videoId}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            val city = City(cityName, videoId)
            
            VideoPlayerScreen(
                city = city,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
