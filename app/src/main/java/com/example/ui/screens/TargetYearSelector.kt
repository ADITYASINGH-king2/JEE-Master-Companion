package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import java.time.Year

/**
 * TargetYearSelector - A dynamically computed target year dropdown selector component.
 * Features auto-decay protections and Material 3 ExposedDropdownMenu styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetYearSelector(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Dynamic Generation: Use Kotlin's 'java.time.Year.now().value' to fetch current system year
    val currentYear = remember { Year.now().value }

    // 2. Array Logic: Dynamically generate list of 4 options (Current, +1, +2, +3)
    val yearsList = remember(currentYear) {
        listOf(currentYear, currentYear + 1, currentYear + 2, currentYear + 3)
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedYear.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Target JEE Year") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("target_year_dropdown_trigger")
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.testTag("target_year_dropdown_menu")
        ) {
            yearsList.forEach { year ->
                DropdownMenuItem(
                    text = { Text(text = year.toString(), style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    },
                    modifier = Modifier.testTag("target_year_option_$year"),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
