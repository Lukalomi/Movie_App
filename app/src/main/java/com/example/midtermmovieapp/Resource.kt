package com.example.midtermmovieapp

sealed class Resource<T:Any> {
    data class Success<T:Any>(val data: T): Resource<T>()
    data class Error<T:Any>(val errorMsg: String) : Resource<T>()
    data class Loader<T:Any>(val isLoading: Boolean): Resource<T>()
}

