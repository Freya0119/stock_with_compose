package com.example.stockdemo_20250303

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockdemo_20250303.net.MsgArray
import com.example.stockdemo_20250303.ui.theme.StockDemo_20250303Theme
import com.example.stockdemo_20250303.view.StockDetail
import com.example.stockdemo_20250303.view.StockTextSingleLine
import com.example.stockdemo_20250303.view.StockTextAlignCenter
import com.example.stockdemo_20250303.view.TextFieldLabel
import com.example.stockdemo_20250303.viewModel.SORT_TYPE
import com.example.stockdemo_20250303.viewModel.StockViewModel

const val INTERCEPTOR_LOG = "INTERCEPTOR_LOG"
const val TEST_LOG = "TEST_LOG"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockDemo_20250303Theme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()

    val viewModel = StockViewModel()

    var title by rememberSaveable { mutableStateOf("Main") }
    var icon by rememberSaveable { mutableStateOf(Icons.Filled.Home) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = { Icon(imageVector = icon, contentDescription = "None") },
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                scrolledContainerColor = Color.Red,
                navigationIconContentColor = Color.Yellow,
                titleContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = Color.Blue
            )
        )
    }) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
//                    todo: view狀態 圖標控制
                    title = "Main"
                    icon = Icons.Filled.Home
                    Content(navController, viewModel)
                }
                composable("detail/{stockId}") { backStackEntry ->
                    title = "Detail"
                    icon = Icons.Filled.Info
                    backStackEntry.arguments?.getString("stockId")?.let { stockId ->
                        StockDetail(stockId)
                    }
                }
            }
        }
    }
}

@Composable
fun Content(navController: NavController, viewModel: StockViewModel) {
    Column {
        InputText(viewModel = viewModel)
        StocksList(viewModel, navController)
    }
}

@Composable
fun InputText(viewModel: StockViewModel) {
    val focusManager = LocalFocusManager.current    // todo: 共用焦點狀態

    var stockId by remember { mutableStateOf("") }
    var isAddSuccess by remember { mutableStateOf<Boolean?>(null) }

    Row(modifier = Modifier.height(IntrinsicSize.Max)) {
        Box(modifier = Modifier
            .fillMaxWidth(fraction = 0.7f)
            .align(Alignment.CenterVertically)) {
            OutlinedTextField(
                value = stockId,
                onValueChange = { new -> stockId = new },
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused) { focusManager.clearFocus() }
                },
                label = { TextFieldLabel(stockId) },
                maxLines = 1
            )
        }
        Button(
            onClick = {
                viewModel.addStocks(stockId) { result ->
                    isAddSuccess = result
                }
                      },
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(8.0.dp))
                .align(Alignment.CenterVertically),
            shape = ButtonDefaults.shape
        ) {
            Text(text = "新增")
        }
    }

    isAddSuccess?.let { ResponseDialog(isAddSuccess!!) { isAddSuccess = null } }
}

@Composable
fun ResponseDialog(result: Boolean, onDismiss: () -> Unit) {
    val showText = if(result) {
        "新增成功"
    } else {
        "新增失敗"
    }

    AlertDialog(
        onDismissRequest = {} ,
        confirmButton = { Button(onClick = { onDismiss() }) { Text(text = "關閉") } },
        title = { Text(text = "新增結果") },
        text = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(), contentAlignment = Alignment.Center) {
                Text(text = showText)
            }
        },
    )
}

@Composable
fun StocksList(viewModel: StockViewModel, navController: NavController) {
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
            Stocks(stocksData = stocks!!, object : (String) -> Unit {
                override fun invoke(p1: String) {
                    navController.navigate("detail/${Uri.encode(p1)}")
                }
            })
        }
    } else {
        Error()
    }
}

@Composable
fun Stocks(stocksData: List<MsgArray>, clicker: (String) -> Unit) {
    Log.e(TEST_LOG, "Stocks recompose.")

    val positionState = rememberLazyListState()
    val firstScrollPosition by remember { derivedStateOf { positionState.firstVisibleItemIndex } }

    LaunchedEffect(firstScrollPosition) {
        Log.e(TEST_LOG, "Position: ${firstScrollPosition}")
    }

    if (stocksData.isNotEmpty()) {
        LazyColumn(state = positionState) {
            items(count = stocksData.size) { i ->
                StockDetail(stock = stocksData[i], clicker)
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
fun StockDetail(stock: MsgArray, clicker: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { stock.股票代號?.let { clicker(it) } },
        horizontalArrangement = Arrangement.Center
    ) {
        Box(Modifier.weight(1f)) { StockTextSingleLine(text = "${stock.公司簡稱}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.當前盤中成交價}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.當前盤中盤成交量}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.最高價格}") }
        Box(Modifier.weight(1f)) { StockTextAlignCenter(text = "${stock.最低價格}") }
    }
}