package com.example.machemol

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import com.google.gson.Gson
import android.content.Context
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegustationListScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = MachemolDatabase.getInstance(context).degustationDao()
    val degustationen by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Snackbar-Host zur Anzeige von Bestätigungen
    val snackbarHostState = remember { SnackbarHostState() }
    //Gruppieren
    val gruppiert = degustationen.groupBy { it.deguname }
    val gruppenStatus = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            Text("📋 Alle Degustationen", fontSize = 22.sp)
            Button(
                onClick = {
                        navController.navigate("degustation")
                    },
                modifier = Modifier.align(Alignment.End)) {
                Text("🍷 Degustationen erfassen")
            }
            Button(
                onClick = {
                    scope.launch {
                        val exportList = dao.getAllOnce()
                        val json = Gson().toJson(exportList)

                        val filename = "degustationen_export.json"
                        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                        outputStream.write(json.toByteArray())
                        outputStream.close()

                        snackbarHostState.showSnackbar("Daten wurden exportiert: $filename")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Exportieren")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                gruppiert.forEach { (gruppenname, liste) ->
                    val istOffen = gruppenStatus.getOrPut(gruppenname) { false }

                    item {
                        // Gruppen-Kopfzeile mit Toggle-Funktion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    gruppenStatus[gruppenname] = !istOffen
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🧾 $gruppenname",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(if (istOffen) "▾" else "▸", fontSize = 20.sp)
                        }
                    }

                    // Zeige nur Einträge, wenn Gruppe offen ist
                    if (istOffen) {
                        items(liste, key = { it.id }) { d ->
                            var showDialog by remember { mutableStateOf(false) }

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("📅 ${d.degudatum} – ${d.wein}", fontSize = 16.sp)
                                    Text("🍷 Winzer: ${d.winzer}")
                                    Text("🎨 Farbe: ${d.farbe}, Jahrgang: ${d.jahrgang}")
                                    Text("👃 Nase: ${d.nase}")
                                    Text("💪 Körper: ${d.koerper}")
                                    Text("🏁 Abgang: ${d.abgang}")
                                    Text("🍽 Essen: ${d.essen}")
                                    Text("📝 Kommentar: ${d.kommentar}")

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { showDialog = true }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Löschen")
                                        }
                                        IconButton(onClick = {
                                            navController.navigate("degustation_edit/${d.id}")
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                                        }
                                    }

                                    if (showDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showDialog = false },
                                            title = { Text("Löschen bestätigen") },
                                            text = { Text("Möchtest du diese Degustation wirklich löschen?") },
                                            confirmButton = {
                                                TextButton(onClick = {
                                                    scope.launch {
                                                        dao.delete(d)
                                                        snackbarHostState.showSnackbar("🗑️ Degustation gelöscht")
                                                    }
                                                    showDialog = false
                                                }) {
                                                    Text("Löschen")
                                                }
                                            },
                                            dismissButton = {
                                                TextButton(onClick = { showDialog = false }) {
                                                    Text("Abbrechen")
                                                }
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
    }
}
