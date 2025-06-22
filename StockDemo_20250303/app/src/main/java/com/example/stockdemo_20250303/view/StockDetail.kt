package com.example.stockdemo_20250303.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockdemo_20250303.companion_object.CompanionTime
import com.example.stockdemo_20250303.net.DailyStock
import com.example.stockdemo_20250303.net.Resource
import com.example.stockdemo_20250303.net.Result
import com.example.stockdemo_20250303.viewModel.StockDetailViewModel

// todo: 加入 dagger
// todo: 分開 主畫面 和 detail 的股票
@Composable
fun StockDetail(stockId: String) {
    val detailViewModel = StockDetailViewModel()
    val stockDetail by detailViewModel.stock.collectAsState()

    LaunchedEffect(stockId) {
        detailViewModel.getStock(stockId)
    }

    when (stockDetail) {
        is Resource.Success -> {
            StockDetailPage((stockDetail as Resource.Success).result)
        }
        is Resource.Fail -> {
            Error((stockDetail as Resource.Fail).errorMsg)
        }
        is Resource.Loading<*> -> {
            Loading()
        }
    }
}

@Composable
fun StockDetailPage(stock: DailyStock?) {

    val detailPadding = 16.0.dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(detailPadding), contentAlignment = Alignment.Center) {

        val stockItem = stock?.chart?.result?.get(0)

        if (stockItem == null) {
            Text(text = "沒有資料")
        } else {
            Column {
                StockHeader(stockItem)
                StockChartView(stockItem)
            }
        }
    }
}

@Composable
fun StockHeader(stock: Result) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(text = "${stock.meta?.regularMarketTime}", modifier = Modifier.align(Alignment.BottomEnd), fontSize = 14.0.sp)
        Column {
//        todo: appBar
//        // stockName(stockId)
//        Text(text = "${stock.meta?.shortName}(${stock.meta?.symbol})")
            // price
            Text(text = "${stock.meta?.regularMarketPrice}", fontSize = 22.0.sp)
            Row(verticalAlignment = Alignment.Bottom) {
//                todo: 漲跌幅
                Text(text = "+5.00", fontSize = 16.0.sp)
                Text(text = "(+1.00%)", fontSize = 12.0.sp)
            }
        }
    }
}

@Composable
fun StockChartView(stock: Result) {
    val paddingValue = 32.0.dp

    Box {
        // chart
        StockChart(stock)
//        todo: 柱圖 grid線
        // x
        val timeList = listOf("9:00", "", "10:00", "", "11:00", "", "12:00", "", "13:00", "13:30")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(paddingValue)
                .padding(PaddingValues(horizontal = paddingValue))
                .align(Alignment.BottomStart)
                .background(Color.Red)
        ) {
            for (time in timeList) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    XText(text = time)
                }
            }
        }
        // y, 字體縮放
        val high = stock.chartHighest
        val low = stock.chartLowest
        val interval = (high - low) / 7
        val value = listOf(high, high - interval, high - interval * 2, high - interval * 3, high - interval * 4, high - interval * 5, low)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(paddingValue)
                .padding(PaddingValues(vertical = paddingValue))
                .align(Alignment.TopEnd)
                .background(Color.Blue)
        ) {
            for (i in 0..6) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(text = "${value[i]}")
                }
            }
        }
    }
}

@Composable
fun StockChart(stock: Result) {
    val timeList = CompanionTime.times
    val close = List<Float?>(timeList.size) { i ->
        stock.closeList[timeList[i]]
    }

    val minY = stock.chartLowest
    val maxY = stock.chartHighest

    val minToMax = maxY - minY

    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
    ) {
        val width = size.width
        val height = size.height
        val space = width / close.size

        val path = Path().apply {
            for (index in close.indices) {
                val price = close[index] ?: continue

                val range = (price - minY) / minToMax   // 將 value 映射到 [0,1] 之間的相對位置

                val x = index * space
                val y = height * (1f - range)           // 反轉 Y 軸，使數值較大的點出現在上方（因為畫布的 Y 軸是從上到下遞增）

                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        drawPath(path = path, color = Color.Yellow, style = Stroke(width = 3f))
        // x軸，y軸
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height)
            },
            color = Color.DarkGray,
            style = Stroke(width = 5f)
        )
        drawPath(
            path = Path().apply {
                moveTo(0f, height)
                lineTo(width, height)
            },
            color = Color.DarkGray,
            style = Stroke(width = 5f)
        )
        // previous price
        stock.meta?.previousClose?.let { price ->
            val range = (price - minY) / minToMax
            val yValue = height * (1f - range)

            val previousPricePath = getHorizontalDashedLine(yValue, width)
            drawPath(
                path = previousPricePath,
                color = Color.LightGray,
                style = Stroke(width = 3f)
            )
        }
//        todo: line value(一套value的計算公式)
        val high = stock.chartHighest
        val low = stock.chartLowest
        val interval = (high - low) / 7
        val value = listOf(high, high - interval, high - interval * 2, high - interval * 3, high - interval * 4, high - interval * 5, low)
        for (tick in value) {
            tick.let { price ->
                val range = (price - minY) / minToMax
                val yValue = height * (1f - range)

                val previousPricePath = getHorizontalDashedLine(yValue, width)
                drawPath(
                    path = previousPricePath,
                    color = Color.Blue,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

fun getHorizontalDashedLine(value: Float, width: Float): Path {
    val lineWidth = 16f
    val interval = 24f

    return Path().apply {
        moveTo(0f, value)

        var point = 0f

        while (point < width) {
            lineTo(point + lineWidth, value)
            moveTo(point + interval, value)
            point += interval
        }
    }
}