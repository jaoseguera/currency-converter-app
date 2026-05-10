package com.simpleapp.currencyconverter.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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