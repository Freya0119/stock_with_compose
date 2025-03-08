package com.example.stockdemo_20250303.net
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

@Serializable
data class RealTimeStock(
    @SerializedName("msgArray")
    val msgArray: List<MsgArray?>?,
//    @SerializedName("cachedAlive")
//    val cachedAlive: Int?,
//    @SerializedName("exKey")
//    val exKey: String?,
//    @SerializedName("queryTime")
//    val queryTime: QueryTime?,
//    @SerializedName("referer")
//    val referer: String?,
//    @SerializedName("rtcode")
//    val rtcode: String?,
//    @SerializedName("rtmessage")
//    val rtmessage: String?,
//    @SerializedName("userDelay")
//    val userDelay: Int?
)

@Serializable
data class MsgArray(
    @SerializedName("a")
    val 揭示賣價: String?,  // (從低到高，以_分隔資料)
    @SerializedName("b")
    val 揭示買價: String?,  // (從高到低，以_分隔資料)
    @SerializedName("c")
    val 股票代號: String?,
    @SerializedName("d")
    val 最近交易日期: String?,    // （YYYYMMDD）
    @SerializedName("f")
    val 揭示賣量: String?,  // (配合a，以_分隔資料)
    @SerializedName("g")
    val 揭示買量: String?,  // (配合b，以_分隔資料)
    @SerializedName("h")
    val 最高價格: String?,
    @SerializedName("l")
    val 最低價格: String?,
    @SerializedName("n")
    val 公司簡稱: String?,
    @SerializedName("nf")
    val 公司全名: String?,
    @SerializedName("o")
    val 開盤價格: String?,
    @SerializedName("t")
    val 最近成交時刻: String?,    // （HH:MI:SS）
    @SerializedName("tlong")
    val 資料更新時間: String?,    // （單位：毫秒）
    @SerializedName("tv")
    val 當前盤中盤成交量: String?,
    @SerializedName("u")
    val 漲停價: String?,
    @SerializedName("v")
    val 累積成交量: String?,
    @SerializedName("w")
    val 跌停價: String?,
    @SerializedName("y")
    val 昨日收盤價格: String?,
    @SerializedName("z")
    val 當前盤中成交價: String?,
//    @SerializedName("ex")
//    val ex: String?,
//    @SerializedName("ch")
//    val ch: String?,
//    @SerializedName("fv")
//    val fv: String?,
//    @SerializedName("i")
//    val i: String?,
//    @SerializedName("ip")
//    val ip: String?,
//    @SerializedName("it")
//    val `it`: String?,
//    @SerializedName("key")
//    val key: String?,
//    @SerializedName("m%")
//    val m: String?,
//    @SerializedName("mt")
//    val mt: String?,
//    @SerializedName("oa")
//    val oa: String?,
//    @SerializedName("ob")
//    val ob: String?,
//    @SerializedName("ot")
//    val ot: String?,
//    @SerializedName("ov")
//    val ov: String?,
//    @SerializedName("oz")
//    val oz: String?,
//    @SerializedName("p")
//    val p: String?,
//    @SerializedName("pid")
//    val pid: String?,
//    @SerializedName("ps")
//    val ps: String?,
//    @SerializedName("pz")
//    val pz: String?,
//    @SerializedName("s")
//    val s: String?,
//    @SerializedName("ts")
//    val ts: String?,
//    @SerializedName("@")
//    val x: String?,
//    @SerializedName("^")
//    val x: String?,
//    @SerializedName("#")
//    val x: String?,
//    @SerializedName("%")
//    val x: String?,
)

@Serializable
data class QueryTime(
    @SerializedName("sessionFromTime")
    val sessionFromTime: Int?,
    @SerializedName("sessionLatestTime")
    val sessionLatestTime: Int?,
    @SerializedName("sessionStr")
    val sessionStr: String?,
    @SerializedName("showChart")
    val showChart: Boolean?,
    @SerializedName("stockInfo")
    val stockInfo: Int?,
    @SerializedName("stockInfoItem")
    val stockInfoItem: Int?,
    @SerializedName("sysDate")
    val sysDate: String?,
    @SerializedName("sysTime")
    val sysTime: String?
)