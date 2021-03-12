package com.example.naturescart.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.naturescart.model.CartDetail
import com.google.gson.Gson

class Persister(context: Context) {

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {

        var instance: Persister? = null

        fun with(context: Context): Persister {
            if (instance == null)
                instance = Persister(context)
            return instance!!
        }
    }

    fun persist(key: String, data: String?) {
        prefs.edit().putString(key, data).apply()
    }

    fun persist(key: String, data: Long) {
        prefs.edit().putLong(key, data).apply()
    }

    fun persist(key: String, data: Float) {
        prefs.edit().putFloat(key, data).apply()
    }

    fun persist(key: String, data: Boolean) {
        prefs.edit().putBoolean(key, data).apply()
    }

    fun getPersisted(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }

    fun getPersistedLong(key: String): Long {
        return prefs.getLong(key, 0)
    }

    fun getPersistedFloat(key: String): Float {
        return prefs.getFloat(key, 0f)
    }

    fun getPersistedBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun getCartQuantity(productId: Long?): Int {
        val cartData = prefs.getString(Constants.cartPersistenceKey, null)
        if (cartData != null) {
            val cartDetail = Gson().fromJson(cartData, CartDetail::class.java)
            cartDetail.items?.forEach {
                if (it.product?.id == productId)
                    return it.quantity ?: 0
            }
        }
        return 0
    }

}