package com.example.machemol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(navController: NavController) {
    val dashboardItems = listOf(
        DashboardItem("Degustation erfassen", Icons.Default.Home, "degustation"),
        DashboardItem("Degustation anzeigen", Icons.Default.Home, "degustation_list"),
        DashboardItem("Weinkeller", Icons.Default.Home, "wine"),
        DashboardItem("Gefrierschrank", Icons.Default.Home, "freezer"),
        DashboardItem("ToDos", Icons.Default.Home, "todos"),
        DashboardItem("Menüplan", Icons.Default.Home, "menu"),
        DashboardItem("Haus", Icons.Default.Home, "house"),
        DashboardItem("Kontakte", Icons.Default.Home, "contacts"),
        DashboardItem("Tiktaktor", Icons.Default.Home, "tiktaktor")
    )

    val columns = if (LocalConfiguration.current.screenWidthDp < 600) 2 else 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Willkommen bei Machemol", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dashboardItems) { item ->
                DashboardTile(item = item, onClick = {
                    navController.navigate(item.route)
                })
            }
        }
    }
}

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DashboardTile(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = item.icon, contentDescription = item.title, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.title)
        }
    }
}
