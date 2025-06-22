package com.example.stockdemo_20250303.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockdemo_20250303.TEST_LOG
import com.example.stockdemo_20250303.companion_object.CompanionTime
import com.example.stockdemo_20250303.net.ApiClient
import com.example.stockdemo_20250303.net.DailyStock
import com.example.stockdemo_20250303.net.Resource
import com.example.stockdemo_20250303.net.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StockDetailViewModel : ViewModel() {
    private val yFinanceClient = ApiClient().yFinance.create(RetrofitClient::class.java)

    private var _stockDetail : MutableStateFlow<Resource<DailyStock>> = MutableStateFlow(Resource.Loading())
    val stock = _stockDetail.asStateFlow()

    fun getStock(stockId: String) {
        viewModelScope.launch {
            stockFlow(stockId)
                .catch { e: Throwable ->
                    _stockDetail.value = Resource.Fail(e.message ?: "Loading error.")
                }
                .collectLatest { resource ->
                    _stockDetail.value = resource
                }
        }
    }

private fun stockFlow(stockId: String) = flow<Resource<DailyStock>> {
        while (true) {
            Log.e(TEST_LOG, "Flow collecting: ${stockId}")
//            todo: 參數控制 in dagger
            val startTime = CompanionTime.startTime
            val endTime = CompanionTime.getNowTime()
            emit(
                yFinanceClient.getDailyStockResource(
                    stockId = stockId,
                    start = "$startTime",
                    end = "$endTime",
                    interval = "1m",
                    events = "history"
                )
            )

            delay(2000)
        }
    }
}