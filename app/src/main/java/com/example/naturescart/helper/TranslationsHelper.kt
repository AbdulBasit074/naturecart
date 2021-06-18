package com.example.naturescart.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONObject





class TranslationsHelper private constructor(context: Context) {
    val translationsStoreKey = "translationsStoreKey"
    private val selectedLanguageStoreKey = "selectedLanguageStoreKey"
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private lateinit var arabicTranslations: JSONObject
    private lateinit var englishTranslations: JSONObject


    companion object {
        private var instance: TranslationsHelper? = null

        fun getInstance(context: Context): TranslationsHelper {
            if (instance == null) {
                instance = TranslationsHelper(context)
            }
            return instance!!
        }

        fun getInstanceWithoutContext(): TranslationsHelper? {
            return instance
        }
    }

    fun storeTranslations(translations: String?) {
        var previousTranslations = prefs.getString(translationsStoreKey, null)
        if (translations != null) {
            prefs.edit().putString(translationsStoreKey, translations).apply()
            previousTranslations = translations
        }
        try {
            val allTranslations = JSONObject(previousTranslations!!)
            arabicTranslations = allTranslations.getJSONObject("ar")
            englishTranslations = allTranslations.getJSONObject("en")
        } catch (e: Exception) {
            arabicTranslations = JSONObject("{data:\"nill\"}")
            englishTranslations = JSONObject("{data:\"nill\"}")
        }

    }

    fun getTranslation(key: String): String {
        return if (getLocale() == "ar") {
            if (::arabicTranslations.isInitialized && arabicTranslations.has(key))
                arabicTranslations[key] as String
            else
                ""
        } else {
            if (::englishTranslations.isInitialized && englishTranslations.has(key))
                englishTranslations[key] as String
            else
                ""
        }
    }

    fun setLanguage(language: String) {
        prefs.edit().putString(selectedLanguageStoreKey, language).apply()
        prefs.edit().putString(Constants.localeKey, getLocale()).apply()
    }
    fun getLanguage(): String {
        return prefs.getString(selectedLanguageStoreKey, "en")!!
    }

    fun getLocale(): String {
        return if (getLanguage() == "en") "en" else "ar"
    }


}