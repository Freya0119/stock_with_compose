package com.example.stockdemo_20250303.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockdemo_20250303.TEST_LOG
import com.example.stockdemo_20250303.companion_object.CompanionTime
import com.example.stockdemo_20250303.net.ApiClient
import com.example.stockdemo_20250303.net.DailyStock
import com.example.stockdemo_20250303.net.MsgArray
import com.example.stockdemo_20250303.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
//    todo: baseUrl 不一樣
    val twseClient = ApiClient().twse.create(RetrofitClient::class.java)
    val yFinanceClient = ApiClient().yFinance.create(RetrofitClient::class.java)

    private val _stocksState = MutableStateFlow<List<MsgArray>?>(null)                // 即時股價
    val stocksState = _stocksState.asStateFlow()
    private val stocksMap = MutableStateFlow(mutableMapOf<String, MutableList<MsgArray>>()) // 歷史股價

    private var stockType = SORT_TYPE.CODE
    private var stockCondition = SORT_CONDITION.NORMAL

    private val stocks = mutableListOf<String>()
//    todo: 舊_換成 Resource<T>
fun getDailyStock(stockId: String, response: (DailyStock) -> Unit) {
        viewModelScope.launch {
            try {
                val startTime = CompanionTime.startTime
                val endTime = CompanionTime.getNowTime()

                val res = yFinanceClient.getDailyStock(stockId = stockId, start = "$startTime", end = "$endTime", interval = "1m", events = "history")
                val stock = res.chart?.result?.get(0)
                val timeList = stock?.timestamp
                val chartLine = stock?.indicators?.adjclose?.get(0)?.adjclose
                val priceModel = stock?.indicators?.quote

                Log.e(TEST_LOG, "Daily success. Code: ${stock?.meta?.symbol}")

                response(res)
            } catch (e: Exception) {
                Log.e(TEST_LOG, "Daily fail: ${e.message}")
            }
        }
    }
//    列表
    fun addStocks(stockId: String, callback: (Boolean) -> Unit) {
        var response = false

        if (!stocks.contains(stockId)) {
            viewModelScope.launch {
                try {
                    val body = twseClient.getTodayStocks("tse_${stockId}.tw")
                    if (body.msgArray?.get(0)?.公司簡稱 != null && !stocks.contains(stockId)) {
                        response = true
                    }

                    if (response) {
                        stocks.add(stockId)
                        getStocks(stocks)
                    }
                } catch (e: Exception) {
                    Log.e(TEST_LOG, "Check new stock error: ${e.message}")
                }

                callback(response)
            }
        } else {
            callback(false)
        }
    }

    fun getStocks(stockIds: List<String>) {
        val query = getQuery(stockIds)

        viewModelScope.launch {
//            while (isActive) {
                try {
                    // "tse_2330.tw|tse_1550.tw"
                    val body = twseClient.getTodayStocks(query)
                    body.msgArray?.let { data ->
                        _stocksState.value = sortedList(data.filterNotNull().filter { it.公司簡稱 != null })

                        stocksMap.update { map ->
                            map.apply { // apply修該自身
                                data.forEach { stock ->
                                    stock?.股票代號?.let { stockCode ->
                                        computeIfAbsent(stockCode) { key ->
                                            mutableListOf() }.add(stock)
                                    }
                                }
                            }
                        }
//                        Log.e("API_CLIENT", "Map update: ${stocksMap.value["2330"]?.size}") // test list.size
                    }
                } catch (e: Exception) {
                    Log.e(TEST_LOG, "${e.message}")
                }
//            }
        }
    }

    private fun getQuery(stockIds: List<String>):String {
        return stockIds.joinToString("|") { "tse_${it}.tw" }
    }
//    ----------排列功能----------
    private fun sortedList(dataList:List<MsgArray>): List<MsgArray> {
        return when(stockType to stockCondition) {
            SORT_TYPE.OPEN to SORT_CONDITION.HIGH -> desSortByOpenPrice(dataList)
            SORT_TYPE.OPEN to SORT_CONDITION.LOW -> sortByOpenPrice(dataList)
            SORT_TYPE.HIGHEST to SORT_CONDITION.HIGH -> desSortByHighPrice(dataList)
            SORT_TYPE.HIGHEST to SORT_CONDITION.LOW -> sortByHighPrice(dataList)
            SORT_TYPE.LOWEST to SORT_CONDITION.HIGH -> desSortByLowPrice(dataList)
            SORT_TYPE.LOWEST to SORT_CONDITION.LOW -> sortByLowPrice(dataList)
            else -> sortByCode(dataList)
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
        _stocksState.value?.let { data ->
            _stocksState.value = sortedList(data)
        }
    }

    private fun sortByOpenPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedBy { it.開盤價格?.toFloatOrNull() }
    }

    private fun desSortByOpenPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedByDescending { it.開盤價格?.toFloatOrNull() }
    }

    private fun sortByHighPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedBy { it.最高價格?.toFloatOrNull() }
    }

    private fun desSortByHighPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedByDescending { it.最高價格?.toFloatOrNull() }
    }

    private fun sortByLowPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedBy { it.最低價格?.toFloatOrNull() }
    }

    private fun desSortByLowPrice(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedByDescending { it.最低價格?.toFloatOrNull() }
    }

    private fun sortByCode(dataList: List<MsgArray>): List<MsgArray> {
        return dataList.sortedBy { it.股票代號 }
    }
}