package com.example.stockdemo_20250303.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StockTextSingleLine(text: String) {
    Text(text = text, softWrap = true, maxLines = 1)
}

@Composable
fun StockTextAlignCenter(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        StockTextSingleLine(text = text)
    }
}

@Composable
fun TextFieldLabel(text: String) {
    Text(text = "請輸入上市股號: $text", color = Color.LightGray)
}