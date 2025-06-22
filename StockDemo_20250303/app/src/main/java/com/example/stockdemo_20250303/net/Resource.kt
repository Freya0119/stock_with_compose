package com.example.stockdemo_20250303.net

sealed class Resource<T> {
    data class Success<T>(val result: T) : Resource<T>()
    class Fail<T>(val errorMsg: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}