package com.example.machemol

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("app_preferences")

object AppPreferences {
    val LAST_DEGUSTATION_NAME = stringPreferencesKey("last_degustation_name")

    fun getLastName(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[LAST_DEGUSTATION_NAME] ?: ""
        }
    }

    suspend fun saveLastName(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_DEGUSTATION_NAME] = name
        }
    }
}
