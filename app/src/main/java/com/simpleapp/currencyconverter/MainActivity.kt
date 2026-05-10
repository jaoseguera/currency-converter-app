package com.simpleapp.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simpleapp.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                val viewModel: CurrencyViewModel = viewModel()
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConverterScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ConverterScreen(viewModel: CurrencyViewModel) {
    LaunchedEffect(viewModel.fromCurrency) {
        viewModel.fetchRates()
    }

    Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
        TextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AdvancedCurrencyPicker(
            label = "From",
            selected = viewModel.fromCurrency,
            currencies = viewModel.allCurrencies,
            onSelection = { viewModel.fromCurrency = it }
        )

        AdvancedCurrencyPicker(
            label = "To",
            selected = viewModel.toCurrency,
            currencies = viewModel.allCurrencies,
            onSelection = { viewModel.toCurrency = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if(viewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Result: %.2f ${viewModel.toCurrency}".format(viewModel.result),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF15803D)
            )
            Text(
                text = "1 ${viewModel.fromCurrency} = ${viewModel.rates[viewModel.toCurrency]} ${viewModel.toCurrency}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedCurrencyPicker(
    label: String,
    selected: String,
    currencies: List<String>,
    onSelection: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    val filteredCurrencies = currencies.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true }
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(text = selected, fontWeight = FontWeight.Bold)
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search currency...") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                items(filteredCurrencies) { currency ->
                    ListItem(
                        headlineContent = { Text(currency) },
                        modifier = Modifier.clickable {
                            onSelection(currency)
                            showSheet = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}