package com.example.stockdemo_20250303.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class ApiClient {
    private val url = "https://openapi.twse.com.tw/v1/exchangeReport/"
    private val todayStockUrl = "https://mis.twse.com.tw/stock/api/"

    val retrofit =
        Retrofit.Builder()
//            內建支援kotlin，解析速度較快，需要ListSerializer手動解析Json(Json String -> Json -> ListSerializer)
//            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(todayStockUrl)
            .build()
}

interface RetrofitClient {
    @GET("STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<Stock>

    @GET("getStockInfo.jsp")
    suspend fun getTodayStocks(@Query("ex_ch") stockId: String): RealTimeStock
}