package com.example.stockdemo_20250303.net

import android.util.Log
import com.example.stockdemo_20250303.INTERCEPTOR_LOG
import com.example.stockdemo_20250303.net.ApiClient.ApiClientEnum
import dagger.Component
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

class ApiClient {
    enum class ApiClientEnum { TWSE, YAHOO }

//    private val url = "https://openapi.twse.com.tw/v1/exchangeReport/"
//    private val todayStockUrl = "https://mis.twse.com.tw/stock/api/"
//    private val stockFromYFinance = "https://query1.finance.yahoo.com/v8/finance/chart/"

    companion object {
//        todo: dagger
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.e(INTERCEPTOR_LOG, "Logging interceptor log: ${message}")
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY   // 記錄完整請求與回應的 body
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
//    // 證交所
//    val twse =
//        Retrofit.Builder()
//            .baseUrl(todayStockUrl)
////            內建支援kotlin，解析速度較快，需要ListSerializer手動解析Json(Json String -> Json -> ListSerializer)
////            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
//            .addConverterFactory(GsonConverterFactory.create())
////            .addConverterFactory(ScalarsConverterFactory.create())
//            .client(client)
//            .build()
//    // yahoo
//    val yFinance =
//        Retrofit.Builder()
//            .baseUrl(stockFromYFinance)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()

    fun createClient(clientType : ApiClientEnum) : Retrofit {
//        todo: for test dagger reference
        @Inject val a =

        val baseUrl = when (clientType) {
            ApiClientEnum.TWSE -> "https://mis.twse.com.tw/stock/api/"
            ApiClientEnum.YAHOO -> "https://query1.finance.yahoo.com/v8/finance/chart/"
        }
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
//            內建支援kotlin，解析速度較快，需要ListSerializer手動解析Json(Json String -> Json -> ListSerializer)
//            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}

interface RetrofitClient {
    @GET("STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<Stock>

    @GET("getStockInfo.jsp")
    suspend fun getTodayStocks(@Query("ex_ch") stockId: String): RealTimeStock

//    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("{stockId}.TW")
    suspend fun getDailyStock(
        @Path("stockId") stockId: String,
        @Query("period1") start: String,
        @Query("period2") end: String,
        @Query("interval") interval: String,
        @Query("events") events: String
    ): DailyStock

    @GET("{stockId}.TW")
    suspend fun getDailyStockResource(
        @Path("stockId") stockId: String,
        @Query("period1") start: String,
        @Query("period2") end: String,
        @Query("interval") interval: String,
        @Query("events") events: String
    ): Resource<DailyStock>
}

// todo: dagger test
@Component
interface YFinanceClient {
    fun getClient(): YFinanceRetrofit
}

class YFinanceRetrofit @Inject constructor() {
    fun getClient(): Retrofit {
        return ApiClient().createClient(ApiClientEnum.YAHOO)
    }
}