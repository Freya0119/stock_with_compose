package com.example.stockdemo_20250303.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockdemo_20250303.net.ApiClient
import com.example.stockdemo_20250303.net.RetrofitClient
import com.example.stockdemo_20250303.net.Stock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    val client = ApiClient().retrofit.create(RetrofitClient::class.java)

    val stocksState = MutableStateFlow<List<Stock>>(emptyList())
    var stockType = SORT_TYPE.CODE
    var stockCondition = SORT_CONDITION.NORMAL

    fun getStocks() {

        viewModelScope.launch {
            try {
                val body = client.getStockDayAll()
                stocksState.value = body
            } catch (e: Exception) {
                Log.e("API_CLIENT", "${e.message}")
            }
        }
    }

    fun sortData(newSortType: SORT_TYPE) {
        if (stockType == newSortType) {
            stockCondition = when(stockCondition) {
                SORT_CONDITION.HIGH -> SORT_CONDITION.LOW
                SORT_CONDITION.LOW -> SORT_CONDITION.NORMAL
                SORT_CONDITION.NORMAL -> SORT_CONDITION.HIGH
            }
        } else {
            stockType = newSortType
            stockCondition = SORT_CONDITION.HIGH
        }

        Log.e("API_CLIENT", "Type:${stockType}, con: $stockCondition")
        when (stockType to stockCondition) {
            SORT_TYPE.OPEN to SORT_CONDITION.HIGH -> desSortByOpenPrice()
            SORT_TYPE.OPEN to SORT_CONDITION.LOW -> sortByOpenPrice()
            SORT_TYPE.HIGHEST to SORT_CONDITION.HIGH -> desSortByHighPrice()
            SORT_TYPE.HIGHEST to SORT_CONDITION.LOW -> sortByHighPrice()
            SORT_TYPE.LOWEST to SORT_CONDITION.HIGH -> desSortByLowPrice()
            SORT_TYPE.LOWEST to SORT_CONDITION.LOW -> sortByLowPrice()
            else -> sortByCode()
        }
    }

    private fun sortByOpenPrice() {
        stocksState.value = stocksState.value.sortedBy { it.openingPrice?.toFloatOrNull() }
    }

    private fun desSortByOpenPrice() {
        stocksState.value = stocksState.value.sortedByDescending { it.openingPrice?.toFloatOrNull() }
    }

    private fun sortByHighPrice() {
        stocksState.value = stocksState.value.sortedBy { it.highestPrice?.toFloatOrNull() }
    }

    private fun desSortByHighPrice() {
        stocksState.value = stocksState.value.sortedByDescending { it.highestPrice?.toFloatOrNull() }
    }

    private fun sortByLowPrice() {
        stocksState.value = stocksState.value.sortedBy { it.lowestPrice?.toFloatOrNull() }
    }

    private fun desSortByLowPrice() {
        stocksState.value = stocksState.value.sortedByDescending { it.lowestPrice?.toFloatOrNull() }
    }

    private fun sortByCode() {
        stocksState.value = stocksState.value.sortedBy { it.code }
    }
}