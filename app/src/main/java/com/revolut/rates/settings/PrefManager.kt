package com.revolut.rates.settings

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context : Context) {

    val defaultStringValue : String = " "
    val defaultIntegerValue : Int = 0
    val defaultFloatValue : Float = 0F
    val defaultDoubleValue : Double = 0.0
    val defaultBooleanValue : Boolean = false
    val prefFileName = "rent_prefs"

    var prefs : SharedPreferences;

    init {
        prefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }

    val SIGN_IN : String = "sign_in"

    val AMOUNT : String = "amount"

    fun setLogin(sign : Boolean){
        with(prefs.edit()){
            putBoolean(SIGN_IN, sign)
            apply()
        }
    }

    fun hasLogin() : Boolean{
        return prefs.getBoolean(SIGN_IN, defaultBooleanValue)
    }

    fun setAmount(am : String){
        with(prefs.edit()){
            putString(AMOUNT, am)
            apply()
        }
    }

    fun getAmount(): String {
        return prefs.getString(AMOUNT, defaultStringValue).toString()
    }
    
}