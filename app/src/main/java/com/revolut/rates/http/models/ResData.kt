package com.revolut.rates.http.models

data class ResData(val base : String, val dates : String) {
    val rates = hashMapOf<String, Any>();
}