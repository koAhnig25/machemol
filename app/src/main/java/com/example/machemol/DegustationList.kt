package com.example.machemol

import android.annotation.SuppressLint
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegustationListScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = MachemolDatabase.getInstance(context).degustationDao()
    val degustationen by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val gruppiert = degustationen.groupBy { it.deguname }
    val gruppenStatus = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding)
        ) {

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 600.dp) {
                    // Kleiner Bildschirm: Buttons untereinander
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🍷 Degustationen", fontSize = 22.sp)
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeuButton(onClick = {
                                navController.navigate("degustation_add")
                            })

                            ExportButton(onClick = {
                                scope.launch {
                                    val exportList = dao.getAllOnce()
                                    val json = Gson().toJson(exportList)
                                    val filename = "degustationen_export.json"
                                    val downloadsDir =
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    val exportFile = File(downloadsDir, filename)
                                    exportFile.writeText(json)
                                    snackbarHostState.showSnackbar("Daten wurden exportiert: $filename")
                                }
                            })
                        }
                    }
                } else {
                    // Großer Bildschirm: Buttons nebeneinander
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🍷 Degustationen", fontSize = 22.sp)
                        Spacer(modifier = Modifier.weight(1f))

                        NeuButton(onClick = {
                            navController.navigate("degustation")
                        })

                        ExportButton(onClick = {
                            scope.launch {
                                val exportList = dao.getAllOnce()
                                val json = Gson().toJson(exportList)
                                val filename = "degustationen_export.json"
                                val downloadsDir =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val exportFile = File(downloadsDir, filename)
                                exportFile.writeText(json)
                                snackbarHostState.showSnackbar("Daten wurden exportiert: $filename")
                            }
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                gruppiert.forEach { (gruppenname, liste) ->
                    val istOffen = gruppenStatus.getOrPut(gruppenname) { false }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { gruppenStatus[gruppenname] = !istOffen }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = gruppenname,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(if (istOffen) "▾" else "▸", fontSize = 20.sp)
                        }
                    }

                    if (istOffen) {
                        items(liste, key = { it.id }) { d ->
                            var showDialog by remember { mutableStateOf(false) }

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("${d.wein} - ${d.jahrgang}", fontSize = 16.sp)
                                    Text("---")
                                    Text("📅 ${d.degudatum}")
                                    Text("🍷 Winzer: ${d.winzer}")
                                    Text("🎨 Farbe: ${d.farbe}")
                                    Text("---")
                                    Text("👃 Nase: ${d.nase}")
                                    Text("💪 Körper: ${d.koerper}")
                                    Text("🏁 Abgang: ${d.abgang}")
                                    Text("---")
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

// 🔘 Wiederverwendbare Buttons
@Composable
fun NeuButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = "Neu")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Neu")
        }
    }
}

@Composable
fun ExportButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Send, contentDescription = "Exportieren")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exportieren")
        }
    }
}
