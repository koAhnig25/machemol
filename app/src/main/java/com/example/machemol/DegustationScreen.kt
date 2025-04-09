package com.example.machemol

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegustationScreen(
    degustation: Degustation? = null,
    onSaved: () -> Unit = {}
) {
    val speichernFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val anzeigenFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val context = LocalContext.current
    val lastName by AppPreferences.getLastName(context).collectAsState(initial = "")

    val selectedDate = remember {
        mutableStateOf(degustation?.degudatum?.let { LocalDate.parse(it) } ?: LocalDate.now())
    }

    var deguname by remember { mutableStateOf(degustation?.deguname ?: "") }
    var winzer by remember { mutableStateOf(degustation?.winzer ?: "") }
    var wein by remember { mutableStateOf(degustation?.wein ?: "") }
    var farbe by remember { mutableStateOf(degustation?.farbe ?: "") }
    var jahrgang by remember { mutableStateOf(degustation?.jahrgang ?: "") }
    var nase by remember { mutableStateOf(degustation?.nase ?: "") }
    var koerper by remember { mutableStateOf(degustation?.koerper ?: "") }
    var abgang by remember { mutableStateOf(degustation?.abgang ?: "") }
    var essen by remember { mutableStateOf(degustation?.essen ?: "") }
    var kommentar by remember { mutableStateOf(degustation?.kommentar ?: "") }
    var markiert by remember { mutableStateOf(false) }
    var anzahlBestellt by remember { mutableStateOf("") }

    val degudatumAnzeige = selectedDate.value.format(anzeigenFormat)
    val degudatum = selectedDate.value.format(speichernFormat)

    val formularGueltig = deguname.isNotBlank() && degudatum.isNotBlank() && wein.isNotBlank() && winzer.isNotBlank()

    val farben = listOf("rot", "weiss", "rosé")
    val aktuellesJahr = LocalDate.now().year
    val jahrgangListe = listOf("") + (1950..aktuellesJahr).toList().reversed()
    val bekannteWinzer = listOf("Mathier", "Weingut XY")

    var expanded by remember { mutableStateOf(false) }
    var jahrExpanded by remember { mutableStateOf(false) }
    var weinExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val dao = MachemolDatabase.getInstance(context).degustationDao()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val weineProWinzer = remember {
        mutableStateMapOf(
            "Mathier" to mutableStateListOf(
                // La Tradition
                "Fendant du Ravin AOC",
                "Johannisberg Weidmannstrunk AOC",
                "Malvoisie La Valaisanne AOC",
                "Muscat La Fiancée AOC",
                "Dôle Blanche Frauenfreude AOC",
                "Œil de Perdrix La Matze AOC",
                "Gamay Memphisto AOC",
                "Sang de l'Enfer de Salquenen AOC",
                "Pinot Noir Nouveau Salquenen AOC",
                "Pinot Noir Lucifer AOC",

                // Terre Promise
                "Ville de Sierre AOC",
                "Ville de Sion AOC",
                "Molignon Terre Promise AOC",

                // Les Pyramides
                "Heida AOC",
                "Humagne Blanc AOC",
                "Petite Arvine AOC",
                "Viognier AOC",
                "Amigne de Vétroz AOC",
                "Humange Rouge AOC",
                "Merlot AOC",
                "Cornolin AOC",
                "Syrah AOC",
                "Pinot Noir Réserve de Salquenen AOC",
                "Pinot Noir de Salquenen  de la famille AOC",

                // Famille Diego Mathier
                "Cuvée Mme Rosmarie Mathier weiss AOC",
                "Thelygenie Valsar weiss AOC",
                "Ermitage Nadia Mathier AOC",
                "Cuvée Mme Rosmarie Mathier rot AOC",
                "Thelygenie Valsar rot AOC",
                "Syrah Diego Mathier AOC",
                "Merlot Nadia Mathier AOC",
                "Humagne Rouge Ferdinand Mathier AOC",
                "Carbanet Sauvignon Adrian Mathier",
                "Cornalin Diego Mathier AOC",
                "Pinot Noir Oskar Mathier non filtré AOC",

                // Les Ambassadeurs
                "Ambassadeur fumé Gros Rhin AOC",
                "L'Ambassadeur D. Mathier blanc AOC",
                "L'Ambassadeur D. Mathier rouge AOC",

                // Diego Mathier – Verrückte Kreation
                "Folissimo AOC",

                // Gigolo – Passpartout
                "Gigolo weiss AOC",
                "Gigolo rouge AOC",

                // Gemma – Edelsteine des Rhonegletschers
                "Topas Vin doux naturel AOC",
                "Rubin Vin doux naturel AOC",
                "Saphir Vin doux naturel AOC",

                // Hospices de Salquenen (50 cl)
                "Assemblage weiss AOC",
                "Petite Arvine AOC",
                "Assemblage rot AOC",
                "Pinot Noir AOC",
                "Humagne Rouge AOC",
                "Syrah AOC",

                // Schaumweine
                "Folie à Deux brut – Goldkapsel",
                "Folie à Deux rosé – Silberkapsel"
            )

        )
    }

    LaunchedEffect(lastName) {
        if (deguname.isBlank()) deguname = lastName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("🍷 Degustation erfassen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SnackbarHost(hostState = snackbarHostState)

        SectionTitle("🧾 Allgemein")
        FormField("Name der Degustation *", deguname) { deguname = it }
        DatePickerField("Datum *", degudatumAnzeige) {
            val picker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    selectedDate.value = LocalDate.of(year, month + 1, day)
                },
                selectedDate.value.year,
                selectedDate.value.monthValue - 1,
                selectedDate.value.dayOfMonth
            )
            picker.show()
        }

        SectionTitle("🍇 Wein")
        FormField("Winzer *", winzer) { winzer = it }
        if (weineProWinzer.containsKey(winzer.trim())) {
            val weinListe = weineProWinzer[winzer.trim()] ?: emptyList()
            DropdownField("Wein *", wein, weinListe, weinExpanded) {
                weinExpanded = it.first
                wein = it.second
            }
        } else {
            FormField("Wein *", wein) { wein = it }
        }

        DropdownField("Farbe", farbe, farben, expanded) {
            expanded = it.first
            farbe = it.second
        }

        DropdownField("Jahrgang", if (jahrgang.isBlank()) "–" else jahrgang, jahrgangListe.map { if (it == "") "– kein Jahrgang –" else it.toString() }, jahrExpanded) {
            jahrExpanded = it.first
            jahrgang = it.second
        }

        SectionTitle("👃 Sensorik")
        FormField("Nase", nase) { nase = it }
        FormField("Körper", koerper) { koerper = it }
        FormField("Abgang", abgang) { abgang = it }

        SectionTitle("🍽 Essen & Notizen")
        FormField("Essen", essen) { essen = it }
        FormField("Kommentar", kommentar) { kommentar = it }

        SectionTitle("⭐ Interesse")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = markiert, onCheckedChange = { markiert = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Interessiert am Wein")
        }
        if (markiert) {
            FormField("Bestellmenge", anzahlBestellt) { anzahlBestellt = it }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val gespeicherte = Degustation(
                    id = degustation?.id ?: 0,
                    deguname = deguname,
                    degudatum = selectedDate.value.format(speichernFormat),
                    winzer = winzer,
                    wein = wein,
                    farbe = farbe,
                    jahrgang = jahrgang,
                    nase = nase,
                    koerper = koerper,
                    abgang = abgang,
                    essen = essen,
                    kommentar = kommentar
                )

                scope.launch {
                    if (degustation == null) {
                        dao.insert(gespeicherte)
                        // Nur bei neuen Einträgen zurücksetzen
                        wein = ""
                        farbe = ""
                        jahrgang = ""
                        nase = ""
                        koerper = ""
                        abgang = ""
                        essen = ""
                        kommentar = ""
                        markiert = false
                        anzahlBestellt = ""

                        // Datum zurücksetzen
                        selectedDate.value = LocalDate.now()

                    }
                    else {
                        dao.update(gespeicherte)
                    }

                    AppPreferences.saveLastName(context, deguname)
                    snackbarHostState.showSnackbar("Degustation gespeichert")
                    onSaved()
                }
            },
            enabled = formularGueltig,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Speichern")
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DatePickerField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        readOnly = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, selected: String, options: List<String>, expandedState: Boolean, onSelectionChanged: (Pair<Boolean, String>) -> Unit) {
    ExposedDropdownMenuBox(expanded = expandedState, onExpandedChange = { onSelectionChanged(it to selected) }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedState) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandedState, onDismissRequest = { onSelectionChanged(false to selected) }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelectionChanged(false to option)
                })
            }
        }
    }
}
