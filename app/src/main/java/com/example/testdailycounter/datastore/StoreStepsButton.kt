package com.example.testdailycounter.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreStepsButton(
    private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("TotalStepsToday")
        val TOTAL_STEPS_KEY = intPreferencesKey("total_steps_today")
    }

    // get saved steps
    val getSteps: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[TOTAL_STEPS_KEY] ?: 0
        }

    // save steps
    suspend fun saveSteps(
        steps: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_STEPS_KEY] = steps
        }
    }
}