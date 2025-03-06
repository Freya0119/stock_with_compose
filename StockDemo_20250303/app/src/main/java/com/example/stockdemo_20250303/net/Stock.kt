package com.example.stockdemo_20250303.net

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stock(
    @SerializedName("Change")
    val change: String?,
    @SerializedName("ClosingPrice")
    val closingPrice: String?,
    @SerializedName("Code")
    val code: String?,
    @SerializedName("HighestPrice")
    val highestPrice: String?,
    @SerializedName("LowestPrice")
    val lowestPrice: String?,
    @SerializedName("Name")
    val name: String?,
    @SerializedName("OpeningPrice")
    val openingPrice: String?,
    @SerializedName("TradeValue")
    val tradeValue: String?,
    @SerializedName("TradeVolume")
    val tradeVolume: String?,
    @SerializedName("Transaction")
    val transaction: String?
)