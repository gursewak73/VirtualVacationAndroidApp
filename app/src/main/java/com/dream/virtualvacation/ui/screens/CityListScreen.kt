package com.dream.virtualvacation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dream.virtualvacation.data.model.City
import com.dream.virtualvacation.data.repository.CitiesRepository
import com.dream.virtualvacation.ui.components.CityCard
import com.dream.virtualvacation.ui.theme.VirtualVacationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val citiesRepository = remember { CitiesRepository(context) }
    var cities by remember { mutableStateOf<List<City>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        cities = citiesRepository.getCities()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Virtual Vacation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Choose your destination",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (cities.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cities) { city ->
                        CityCard(
                            city = city,
                            onClick = { onCityClick(city) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityListScreenPreview() {
    VirtualVacationTheme {
        CityListScreen(
            onCityClick = { /* Preview action */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CityListScreenPreviewWithData() {
    VirtualVacationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Virtual Vacation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Choose your destination",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(getPreviewCities()) { city ->
                            CityCard(
                                city = city,
                                onClick = { /* Preview action */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getPreviewCities(): List<City> {
    return listOf(
        City("Naples 🇮🇹", "IHXZnU2bmc8"),
        City("New York 🇺🇸", "n1xkO0_lSU0"),
        City("Barcelona 🇪🇸", "hbYweUDbtxc"),
        City("Moscow 🇷🇺", "pRm5WYmJIbA"),
        City("Miami 🇺🇸", "O8Td31EnrrI"),
        City("Helsinki 🇫🇮", "sdOAzDb8BAI"),
        City("Beverly Hills 🇺🇸", "Cw0d-nqSNE8"),
        City("Paris 🇫🇷", "FBjjYw-xcdg")
    )
}
