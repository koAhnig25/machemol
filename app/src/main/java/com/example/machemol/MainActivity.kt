package com.example.machemol

// Datenklasse für Degustationen
// import Scrollen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Entity(tableName = "degustationen")
data class Degustation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ← wichtig!
    val deguname: String,
    val degudatum: String,
    val winzer: String,
    val wein: String,
    val farbe: String,
    val jahrgang: String,
    val nase: String,
    val koerper: String,
    val abgang: String,
    val essen: String,
    val kommentar: String,
    val markiert: Boolean = false,
    val anzahlBestellt: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MachemolApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachemolApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Machemol") }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard", // falls du direkt zum neuen Dashboard willst
            modifier = Modifier.padding(padding)
        ) {
            // Hauptscreens
            composable("dashboard") { DashboardScreen(navController) }
            //composable("home") { HomeScreen(navController) }

            // Degustationen
            composable("degustation") { DegustationScreen() }
            composable("degustation_list") { DegustationListScreen(navController) }

            // Weitere Module
            composable("wine") { WineCellarScreen() }
            composable("freezer") { /* TODO: GefrierschrankScreen() */ }
            composable("todos") { /* TODO: TodoScreen() */ }
            composable("menu") { /* TODO: MenuScreen() */ }
            composable("house") { /* TODO: HouseScreen() */ }
            composable("contacts") { /* TODO: ContactsScreen() */ }
            composable("tiktaktor") { /* TODO: TiktaktorScreen() */ }

            // Degustation bearbeiten mit ID
            composable("degustation_edit/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                val dao = MachemolDatabase.getInstance(LocalContext.current).degustationDao()
                val item by dao.getById(id ?: -1).collectAsState(initial = null)

                if (item != null) {
                    DegustationScreen(degustation = item!!) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
