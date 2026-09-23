package com.example.quoterandomizer

import android.Manifest
import android.app.*
import android.content.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent { QuoteApp() }
    }
}

@Composable
fun QuoteApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var quotes by remember { mutableStateOf(Prefs.loadQuotes(context)) }
    var newQuote by remember { mutableStateOf("") }
    var minMinutes by remember { mutableStateOf("60") }
    var maxMinutes by remember { mutableStateOf("180") }
    var enabled by remember { mutableStateOf(Prefs.enabled(context)) }

    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Quote Randomizer") }) }
        ) { pad ->
            Column(
                Modifier.padding(pad).padding(16.dp).fillMaxSize()
            ) {
                Text("Add your own quotes", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newQuote,
                    onValueChange = { newQuote = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type a quote...") },
                    minLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newQuote.isNotBlank()) {
                            quotes = quotes + newQuote.trim()
                            Prefs.saveQuotes(context, quotes)
                            newQuote = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Add Quote") }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minMinutes,
                        onValueChange = { minMinutes = it.filter(Char::isDigit) },
                        label = { Text("Min minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxMinutes,
                        onValueChange = { maxMinutes = it.filter(Char::isDigit) },
                        label = { Text("Max minutes") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (enabled) "Notifications ON" else "Notifications OFF")
                    Switch(
                        checked = enabled,
                        onCheckedChange = {
                            enabled = it
                            Prefs.setEnabled(context, it)
                            if (it) {
                                Scheduler.scheduleNext(
                                    context,
                                    minMinutes.toLongOrNull() ?: 60,
                                    maxMinutes.toLongOrNull() ?: 180
                                )
                            } else Scheduler.cancel(context)
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Notifications appear at a random interval between the two values.")
                Spacer(Modifier.height(16.dp))
                Text("Your quotes (${quotes.size})", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quotes) { quote ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(quote, Modifier.weight(1f))
                                TextButton(onClick = {
                                    quotes = quotes - quote
                                    Prefs.saveQuotes(context, quotes)
                                }) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }
}
