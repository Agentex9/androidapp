// PreRegistroScreen.kt
package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.R
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.PreRegForm
import com.example.proyecto.ui.theme.viewmodels.CountriesViewModel
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyecto.utilities.AVISO_PRIVACIDAD


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneWithCountryField(
    telefono: String,
    onTelefono: (String) -> Unit,
    dialCode: String,
    onDialCode: (String) -> Unit,
    countriesVM: CountriesViewModel,
    enabled: Boolean = true
) {
    val countries by countriesVM.countries.collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(countries) {
        if (dialCode.isBlank() && countries.isNotEmpty()) {
            onDialCode(countries.first().dialCode)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled && countries.isNotEmpty()) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = telefono,
            onValueChange = { onTelefono(it.filter(Char::isDigit)) },
            label = { Text("Número de teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            leadingIcon = {
                AssistChip(
                    onClick = { if (enabled && countries.isNotEmpty()) expanded = true },
                    label = { Text(if (dialCode.isNotBlank()) dialCode else "+") },
                    enabled = enabled
                )
            },
            enabled = enabled,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { c ->
                val code = c.dialCode
                DropdownMenuItem(
                    text = { Text("${c.name?.common.orEmpty()}  $code") },
                    onClick = {
                        onDialCode(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreRegistroScreen(
    generalViewModel: GeneralViewModel = viewModel(),
    countriesVM: CountriesViewModel = viewModel(),
    onDone: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    var showPdf by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    var genero by remember { mutableStateOf("Femenino") }
    val generos = listOf("Femenino", "Masculino")
    var generoMenu by remember { mutableStateOf(false) }

    var telefono by remember { mutableStateOf("") }
    var dialCode by remember { mutableStateOf("") }

    var aceptarPoliticas by remember { mutableStateOf(false) }
    val ui by generalViewModel.preRegistroState.collectAsState()

    val loading by rememberUpdatedState(ui is ResultState.Loading)
    val genderCode = when (genero) {
        "Masculino" -> "M"
        "Femenino" -> "F"
        else -> "" // si tienes "Otro", puedes decidir cómo mapearlo
    }

    LaunchedEffect(ui) {
        if (ui is ResultState.Success) {
            onDone()   // ✅ dispara la navegación
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (showPdf) {
            false -> {
                Spacer(Modifier.height(8.dp))

                Image(
                    painter = painterResource(R.drawable.caritas_bg),
                    contentDescription = "Logo Cáritas",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )

                Text("Pre-registro Cáritas Mty", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = edad,
                    onValueChange = { if (it.all(Char::isDigit) && it.length <= 3) edad = it },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                )
                Spacer(Modifier.height(12.dp))

                // Campo Género
                ExposedDropdownMenuBox(
                    expanded = generoMenu,
                    onExpandedChange = { if (!loading) generoMenu = !generoMenu }
                ) {
                    OutlinedTextField(
                        value = genero,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Género") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoMenu) },
                        modifier = Modifier
                            .menuAnchor(
                                ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = !loading
                            )
                            .fillMaxWidth(),
                        enabled = !loading
                    )
                    ExposedDropdownMenu(
                        expanded = generoMenu,
                        onDismissRequest = { generoMenu = false }
                    ) {
                        generos.forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    genero = g
                                    generoMenu = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Teléfono con lada
                PhoneWithCountryField(
                    telefono = telefono,
                    onTelefono = { telefono = it },
                    dialCode = dialCode,
                    onDialCode = { dialCode = it },
                    countriesVM = countriesVM,
                    enabled = !loading
                )
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = aceptarPoliticas,
                        onCheckedChange = { aceptarPoliticas = it },
                        enabled = !loading
                    )

                    Text(
                        buildAnnotatedString {
                            append("Acepto ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("políticas de privacidad")
                            }
                        },
                        modifier = Modifier.clickable { showPdf = true }
                    )
                }


                Spacer(Modifier.height(24.dp))

                val habilitado =
                    nombre.isNotBlank() &&
                            apellido.isNotBlank() &&
                            telefono.isNotBlank() &&
                            edad.toIntOrNull() != null &&
                            genero.isNotBlank() &&
                            aceptarPoliticas

                Button(
                    onClick = {
                        val fullPhone = dialCode + telefono
                        val req = PreRegForm(
                            first_name = nombre,
                            last_name = apellido,
                            phone_number = fullPhone,
                            age = edad.toInt(),
                            gender = genderCode,
                            privacy_policy_accepted = aceptarPoliticas
                        )
                        generalViewModel.submitPreRegistro(req)
                    },
                    enabled = habilitado && !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loading) CircularProgressIndicator(Modifier.size(24.dp))
                    else Text("Enviar Pre-registro")
                }

                Spacer(Modifier.height(8.dp))

                when (ui) {
                    is ResultState.Error ->
                        Text(
                            text = (ui as ResultState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )

                    else -> {}
                }
            }




            true -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = AVISO_PRIVACIDAD,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = { showPdf = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Listo")
                    }
                }
            }
            /*
            true -> {
                // Show the PDF in the same screen
                Column(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                loadUrl(
                                    "http://20.246.91.21:8001/media/privacy_policies/AVISO_DE_PRIVACIDAD_2025.pdf"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // WebView takes all available vertical space
                    )

                    Button(
                        onClick = { showPdf = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Cerrar PDF")
                    }
                }
            }*/


        }
    }
}

@Preview(showBackground = true, device = "id:pixel_9a", showSystemUi = true)
@Composable
private fun PreRegistroPreview() {
    PreRegistroScreen()
}
