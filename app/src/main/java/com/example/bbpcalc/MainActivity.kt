package com.example.bbpcalc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bbpcalc.ui.theme.BBPcalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BBPcalcTheme(darkTheme = true) {
                Surface(
                    modifier=Modifier.fillMaxSize(),
                    color=MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

data class ItemCost(
    val nails: Int=0,
    val planks: Int=0,
    val logs: Int=0,
    val sheetMetal: Int=0,
    val barbwire: Int=0
)

val items=listOf(
    "Large Wall" to ItemCost(nails=20, planks=11),
    "Small Wall" to ItemCost(nails=20, planks=6),
    "Large Half-Wall" to ItemCost(nails=20, planks=6),
    "Small Half-Wall" to ItemCost(nails=15, planks=4),
    "Large Door" to ItemCost(nails=30, planks=15),
    "Small Door" to ItemCost(nails=30, planks=8),
    "Large Floor/Roof" to ItemCost(nails=20, planks=13),
    "Small Floor/Roof" to ItemCost(nails=10, planks=6),
    "Large Floor/Roof Hatch" to ItemCost(nails=40, planks=21),
    "Foundation" to ItemCost(nails=20, planks=8, logs=2),
    "Large Gate" to ItemCost(nails=34, planks=20),
    "Double Garage Door" to ItemCost(nails=22, planks=12),
    "Single Garage Door" to ItemCost(nails=34, planks=20),
    "Large Window" to ItemCost(nails=25, planks=15),
    "Small Window" to ItemCost(nails=25, planks=10),
    "Large Stairs" to ItemCost(nails=10, planks=5),
    "Small Stairs" to ItemCost(nails=5, planks=5),
    "Large Ramp" to ItemCost(nails=15, planks=8),
    "Chain Link Fence" to ItemCost(nails=30, sheetMetal=4, barbwire=1),
    "Chain Link Gate" to ItemCost(nails=30, sheetMetal=3, barbwire=1)
)

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CalculatorScreen() {
    var quantities by remember { mutableStateOf(mutableMapOf<String, Int>()) }

    val totals = calculateTotals(quantities)

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.padding(12.dp)) {
                Button(
                    onClick = { quantities = mutableMapOf() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear List")
                }
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Resources Needed\nIncluding nails/planks for kits", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Nails: ${totals.nails}")
                        Text("Planks: ${totals.planks}")
                        Text("Logs: ${totals.logs}")
                        Text("Sheet Metal: ${totals.sheetMetal}")
                        Text("Barbwire: ${totals.barbwire}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total Kits: ${totals.kits}")
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(12.dp)) {
            Text("DayZ BaseBuilding T1 Calculator", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp) // leave space for bottom card
            ) {
                items(items) { (itemName, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(itemName, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = (quantities[itemName] ?: 0).toString(),
                            onValueChange = {
                                val num = it.toIntOrNull() ?: 0
                                quantities = quantities.toMutableMap().apply {
                                    this[itemName] = num
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }
        }
    }
}

data class Totals(
    val nails: Int,
    val planks: Int,
    val logs: Int,
    val sheetMetal: Int,
    val barbwire: Int,
    val kits: Int
)

fun calculateTotals(quantities: Map<String, Int>): Totals {
    var nails=0
    var planks=0
    var logs=0
    var sheetMetal=0
    var barbwire=0
    var kits=0

    quantities.forEach { (itemName, count) ->
        val cost = items.find { it.first == itemName }?.second ?: return@forEach
        nails += (cost.nails * count)
        planks += (cost.planks * count)
        logs += (cost.logs * count)
        sheetMetal += (cost.sheetMetal * count)
        barbwire += (cost.barbwire * count)
        kits += count
    }

    // Add kit requirements: each kit=3 nails + 3 planks
    nails += kits * 3
    planks += kits * 3

    return Totals(nails, planks, logs, sheetMetal, barbwire, kits)
}