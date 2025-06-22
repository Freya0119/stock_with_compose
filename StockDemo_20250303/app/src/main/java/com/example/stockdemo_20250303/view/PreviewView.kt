package com.example.stockdemo_20250303.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockdemo_20250303.net.DailyStock

//@Preview
//@Composable
//fun PreviewTest() {
//    StockDetailPage()
//}

@Composable
fun StockDetailPage(stock: DailyStock) {
    val detailPadding = 16.0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(detailPadding),
        contentAlignment = Alignment.Center
    ) {
        Column {
            StockHeader(stock)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(detailPadding))
            StockLineChart()
        }
    }
}

@Composable
fun StockHeader(stock: DailyStock) {
    val outerPadding = 4.dp
    val innerPadding = 4.dp
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(outerPadding)) {
        Text(text = , modifier = Modifier.align(Alignment.BottomEnd), fontSize = 14.0.sp)
        Column(verticalArrangement = Arrangement.Center) {
            Text(text = , fontSize = 22.0.sp)
            Spacer(modifier = Modifier.height(innerPadding))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = , fontSize = 16.0.sp)
                Spacer(modifier = Modifier.width(innerPadding))
                Text(text = , fontSize = 12.0.sp)
            }
        }
    }
}

// todo: line data
//  todo: StockDaily.toLineChart()
@Composable
fun StockLineChart(closePriceList: List<Float>) {
    val paddingValue = 32.0.dp

    Box {
        val rowModifier = Modifier
            .fillMaxWidth()
            .height(paddingValue)
            .padding(PaddingValues(end = paddingValue))
            .align(Alignment.BottomStart)
        TimeRow(rowModifier)

        val columnModifier = Modifier
            .fillMaxHeight()
            .width(paddingValue)
            .padding(PaddingValues(bottom = paddingValue))
            .align(Alignment.TopEnd)
        PriceColumn(modifier = columnModifier, closePriceList = closePriceList)

        StockChart(paddingValue, closePriceList)
    }
}

@Composable
fun StockChart(innerPadding: Dp, dataList: List<Float>) {
    val close = dataList

    val minY = close.min()
    val maxY = close.max()

    val minToMax = maxY - minY

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding, end = innerPadding)
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
        990f.let { price ->
            val range = (price - minY) / minToMax
            val yValue = height * (1f - range)

            val previousPricePath = getHorizontalDashedLine(yValue, width)
            drawPath(
                path = previousPricePath,
                color = Color.LightGray,
                style = Stroke(width = 3f)
            )
        }

        val high = close.max()
        val low = close.min()
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

@Composable
fun TimeRow(modifier: Modifier) {
//    todo: timeList
    val timeList = listOf("9:00", "", "10:00", "", "11:00", "", "12:00", "", "13:00", "13:30")
    Row(modifier) {
        val rowModifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)

        for (time in timeList) {
            Box(rowModifier) { XText(text = time) }
        }
    }
}

@Composable
fun PriceColumn(modifier: Modifier, closePriceList: List<Float>) {
    val high = closePriceList.max()
    val low = closePriceList.min()
    val interval = (high - low) / 7
    val values = listOf(high, high - interval, high - interval * 2, high - interval * 3, high - interval * 4, high - interval * 5, low)

    Column(modifier) {
        val columnModifier = Modifier
            .weight(1f)
            .padding(start = 2.dp, top = 2.dp)

        for (value in values) {
            Box(columnModifier) { XText(text = "$value") }
        }
    }
}