package com.example.machemol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Willkommen bei Machemol", fontSize = 24.sp)

        //Button(onClick = { navController.navigate("wine") }, modifier = Modifier.fillMaxWidth()) {
        //    Text("🍷 Weinkeller anzeigen")
        //}

        Button(onClick = { navController.navigate("degustation") }, modifier = Modifier.fillMaxWidth()) {
            Text("🍷 Degustationen erfassen")
        }
        Button(onClick = { navController.navigate("degustation_list") }, modifier = Modifier.fillMaxWidth()) {
            Text("📋 Degustationen anzeigen")
        }
    }
}