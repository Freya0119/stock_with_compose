package com.example.stockdemo_20250303.net
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DailyStock(
    @SerializedName("chart")
    val chart: Chart?
)

@Serializable
data class Chart(
    @SerializedName("error")
    val error: String?,
    @SerializedName("result")
    val result: List<Result?>?
)

@Serializable
data class Result(
    @SerializedName("indicators")
    val indicators: Indicators?,
    @SerializedName("meta")
    val meta: Meta?,
    @SerializedName("timestamp")
    val timestamp: List<Int?>?
) {
    // Map<TimeStamp, Price>
    val closeList: Map<Int, Float?>
        get() {
            val close = indicators?.quote?.get(0)?.close
            return if (!timestamp.isNullOrEmpty() && !timestamp.contains(null) && timestamp.size == close?.size) {
                timestamp.filterNotNull().zip(close).toMap()
            } else {
                mapOf()
            }
        }

    val chartLowest: Float get() = (meta?.previousClose ?: 0f).coerceAtMost(indicators?.quote?.get(0)?.low?.filterNotNull()?.minOrNull() ?: 0f)
    val chartHighest: Float get() = (meta?.previousClose ?: 0f).coerceAtLeast(indicators?.quote?.get(0)?.high?.filterNotNull()?.maxOrNull() ?: 0f)
}

@Serializable
data class Indicators(
    @SerializedName("adjclose")
    val adjclose: List<Adjclose?>?,
    @SerializedName("quote")
    val quote: List<Quote?>?
)

@Serializable
data class Meta(
    @SerializedName("chartPreviousClose")
    val chartPreviousClose: Float?,
    @SerializedName("previousClose")
    val previousClose: Float?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("currentTradingPeriod")
    val currentTradingPeriod: CurrentTradingPeriod?,
    @SerializedName("dataGranularity")
    val dataGranularity: String?,
    @SerializedName("exchangeName")
    val exchangeName: String?,
    @SerializedName("exchangeTimezoneName")
    val exchangeTimezoneName: String?,
    @SerializedName("fiftyTwoWeekHigh")
    val fiftyTwoWeekHigh: Float?,
    @SerializedName("fiftyTwoWeekLow")
    val fiftyTwoWeekLow: Float?,
    @SerializedName("firstTradeDate")
    val firstTradeDate: Int?,
    @SerializedName("fullExchangeName")
    val fullExchangeName: String?,
    @SerializedName("gmtoffset")
    val gmtoffset: Int?,
    @SerializedName("hasPrePostMarketData")
    val hasPrePostMarketData: Boolean?,
    @SerializedName("instrumentType")
    val instrumentType: String?,
    @SerializedName("longName")
    val longName: String?,
    @SerializedName("priceHint")
    val priceHint: Int?,
    @SerializedName("range")
    val range: String?,
    @SerializedName("regularMarketDayHigh")
    val regularMarketDayHigh: Float?,
    @SerializedName("regularMarketDayLow")
    val regularMarketDayLow: Float?,
    @SerializedName("regularMarketPrice")
    val regularMarketPrice: Float?,
    @SerializedName("regularMarketTime")
    val regularMarketTime: Int?,
    @SerializedName("regularMarketVolume")
    val regularMarketVolume: Int?,
    @SerializedName("shortName")
    val shortName: String?,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("timezone")
    val timezone: String?,
    @SerializedName("validRanges")
    val validRanges: List<String?>?
)

@Serializable
data class Adjclose(
    @SerializedName("adjclose")
    val adjclose: List<Float?>?
)

@Serializable
data class Quote(
    @SerializedName("close")
    val close: List<Float?>?,
    @SerializedName("high")
    val high: List<Float?>?,
    @SerializedName("low")
    val low: List<Float?>?,
    @SerializedName("open")
    val `open`: List<Float?>?,
    @SerializedName("volume")
    val volume: List<Float?>?
)

@Serializable
data class CurrentTradingPeriod(
    @SerializedName("post")
    val post: Post?,
    @SerializedName("pre")
    val pre: Pre?,
    @SerializedName("regular")
    val regular: Regular?
)

@Serializable
data class Post(
    @SerializedName("end")
    val end: Int?,
    @SerializedName("gmtoffset")
    val gmtoffset: Int?,
    @SerializedName("start")
    val start: Int?,
    @SerializedName("timezone")
    val timezone: String?
)

@Serializable
data class Pre(
    @SerializedName("end")
    val end: Int?,
    @SerializedName("gmtoffset")
    val gmtoffset: Int?,
    @SerializedName("start")
    val start: Int?,
    @SerializedName("timezone")
    val timezone: String?
)

@Serializable
data class Regular(
    @SerializedName("end")
    val end: Int?,
    @SerializedName("gmtoffset")
    val gmtoffset: Int?,
    @SerializedName("start")
    val start: Int?,
    @SerializedName("timezone")
    val timezone: String?
)