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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stockdemo_20250303.net.MsgArray
import com.example.stockdemo_20250303.ui.theme.StockDemo_20250303Theme
import com.example.stockdemo_20250303.view.ApiErrorView
import com.example.stockdemo_20250303.view.StockTextSingleLine
import com.example.stockdemo_20250303.view.StockTextAlignCenter
import com.example.stockdemo_20250303.view.TextFieldLabel
import com.example.stockdemo_20250303.viewModel.SORT_TYPE
import com.example.stockdemo_20250303.viewModel.StockViewModel

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
                            .padding(innerPadding),
                        ) {
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
//    viewModel.getStocks(listOf("2330", "0050", "6547"))

    Column(Modifier.padding(8.0.dp)) {
        MainTextField(viewModel = viewModel)
        StocksBody(viewModel)
    }
}

@Composable
fun MainTextField(viewModel: StockViewModel) {
    var stockId by remember { mutableStateOf("") }

    LaunchedEffect(stockId) {
        viewModel.getStocks(listOf(stockId))
    }

    Box {   // todo: style
        OutlinedTextField(
            value = stockId,
            onValueChange = { new -> stockId = new },
            label = { TextFieldLabel(stockId) },
            maxLines = 1
        )
    }
}

@Composable
fun StocksBody(viewModel: StockViewModel) {
    val stocks by viewModel.stocksState.collectAsState()

    if (!stocks.isNullOrEmpty()) {
        Column {
            Row {
                Box(modifier = Modifier.weight(1f)) { StockTextSingleLine("證券名稱") }
                Box(modifier = Modifier.weight(1f)) { StockTextAlignCenter("成交金額") }
                Box(modifier = Modifier.weight(1f)) { StockTextAlignCenter("成交量") }
                Box(modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.sortData(SORT_TYPE.HIGHEST) }) { StockTextAlignCenter("最高價") }
                Box(modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.sortData(SORT_TYPE.LOWEST) }) { StockTextAlignCenter("最低價") }
            }
            Stocks(stocksData = stocks!!)
        }
    } else {
        ApiErrorView()
    }
}

@Composable
fun Stocks(stocksData: List<MsgArray>) {
    Log.e("API_CLIENT", "Recompose")
    val positionState = rememberLazyListState()
    val firstScrollPosition by remember { derivedStateOf { positionState.firstVisibleItemIndex } }

    LaunchedEffect(firstScrollPosition) {
        Log.e("API_CLIENT", "Position: ${firstScrollPosition}")
    }

    if (stocksData.isNotEmpty()) {
        LazyColumn(state = positionState) {
            items(count = stocksData.size) { i ->
                StockDetail(stock = stocksData[i])
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.Center) {
            StockTextSingleLine(text = "No data")
            StockTextSingleLine(text = "No data")
            StockTextSingleLine(text = "No data")
            StockTextSingleLine(text = "No data")
            StockTextSingleLine(text = "No data")
        }
    }
}

@Composable
fun StockDetail(stock: MsgArray) {
    Row(horizontalArrangement = Arrangement.Center) {
        Box(Modifier.weight(1f)) { StockTextSingleLine(text = "${stock.公司簡稱}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.當前盤中成交價}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.當前盤中盤成交量}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.最高價格}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.最低價格}") }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    StockDemo_20250303Theme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.LightGray),
//            contentAlignment = Alignment.Center
//        ) {
//            val stocks = emptyList<Stock>()
//            Stocks(stocksData = stocks)
//        }
//    }
//}