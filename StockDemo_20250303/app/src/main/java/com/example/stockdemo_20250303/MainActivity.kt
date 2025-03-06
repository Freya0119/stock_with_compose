package com.example.stockdemo_20250303

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockdemo_20250303.net.Stock
import com.example.stockdemo_20250303.ui.theme.StockDemo_20250303Theme
import com.example.stockdemo_20250303.viewModel.SORT_CONDITION
import com.example.stockdemo_20250303.viewModel.SORT_TYPE
import com.example.stockdemo_20250303.viewModel.StockViewModel
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockDemo_20250303Theme {
                Scaffold(
                    topBar = { TopAppBar(
                        title = { Text(text = "Stocks") },
                        colors = TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            scrolledContainerColor = Color.Red,
                            navigationIconContentColor = Color.Yellow,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                            actionIconContentColor = Color.Blue
                        )
                    ) },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center) { 
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val viewModel = StockViewModel()
    viewModel.getStocks()

    Box(Modifier.padding(8.0.dp)) {
        StocksBody(viewModel)
    }
}

@Composable
fun StocksBody(viewModel: StockViewModel) {
    val stocks by viewModel.stocksState.collectAsState()

    if (stocks.isNotEmpty()) {
        Column {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = "證券代號", modifier = Modifier
                    .weight(1f)
                    .clickable { },
                    style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
                Text(text = "證券名稱", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "成交股數", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "成交金額", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
                Text(text = "開盤價", modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.sortData(SORT_TYPE.OPEN) },
                    style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
                Text(text = "最高價", modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.sortData(SORT_TYPE.HIGHEST) },
                    style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
                Text(text = "最低價", modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.sortData(SORT_TYPE.LOWEST) },
                    style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "收盤價", modifier = Modifier.weight(1f))
            }
            Stocks(stocksData = stocks)
        }
    } else {
        Text(text = "No state")
    }
}

@Composable
fun StockTextStyle(text: String) {
    Text(text = text, style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
}

@Composable
fun StocksTitle() {
    Row(horizontalArrangement = Arrangement.Center) {
        Box(Modifier.weight(1f)) { StockTextStyle(text = "證券代號") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "證券名稱") }
//            Text(text = "成交股數", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "成交金額", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
        Box(Modifier.weight(1f)) { StockTextStyle(text = "開盤價") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "最高價") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "最低價") }
//            Text(text = "收盤價", modifier = Modifier.weight(1f))
    }
}

@Composable
fun Stocks(stocksData: List<Stock>) {
    val positionState = rememberScrollState()

    LaunchedEffect(positionState.value) {
        Log.e("API_CLIENT", "Position: ${positionState.value}")
    }

    if (stocksData.isNotEmpty()) {
        LazyColumn() {
            items(count = stocksData.size) { i ->
                StockDetail(stock = stocksData[i])
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.Center) {
            StockTextStyle(text = "No data")
            StockTextStyle(text = "No data")
//            Text(text = "成交股數", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "成交金額", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
            StockTextStyle(text = "No data")
            StockTextStyle(text = "No data")
            StockTextStyle(text = "No data")
//            Text(text = "收盤價", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StockDetail(stock: Stock) {
    Row(horizontalArrangement = Arrangement.Center) {
        Box(Modifier.weight(1f)) { StockTextStyle(text = "${stock.code}") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "${stock.name}") }
//            Text(text = "成交股數", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
//            Text(text = "成交金額", modifier = Modifier.weight(1f), style = TextStyle(fontSize = TextUnit.Unspecified), maxLines = 1)
        Box(Modifier.weight(1f)) { StockTextStyle(text = "${stock.openingPrice}") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "${stock.highestPrice}") }
        Box(Modifier.weight(1f)) { StockTextStyle(text = "${stock.lowestPrice}") }
//            Text(text = "收盤價", modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StockDemo_20250303Theme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            val stocks = emptyList<Stock>()
            StocksTitle()
            Stocks(stocksData = stocks)
        }
    }
}