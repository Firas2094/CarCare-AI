package com.firas.carcareai.notification

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.maintenanceReminderDataStore by preferencesDataStore(
    name = "maintenance_reminders"
)

class MaintenanceReminderStore (private val context: Context)  {

    suspend fun wasAlreadySent(
        vehicleId: Long,
        maintenanceType: String,
        reminderSignature: String
    ): Boolean {
        val key = createKey(vehicleId, maintenanceType)
        val preferences = context.maintenanceReminderDataStore.data.first()
        return preferences[key] == reminderSignature
    }

    suspend fun markAsSent(
        vehicleId: Long,
        maintenanceType: String,
        reminderSignature: String
    ) {
        val key = createKey(vehicleId, maintenanceType)

        context.maintenanceReminderDataStore.edit { preferences ->
            preferences[key] = reminderSignature
        }
    }

    suspend fun clearReminder(
        vehicleId: Long,
        maintenanceType: String
    ) {
        val key = createKey(vehicleId, maintenanceType)

        context.maintenanceReminderDataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    private fun createKey(
        vehicleId: Long,
        maintenanceType: String
    ) = stringPreferencesKey(
        "reminder_${vehicleId}_${maintenanceType}"
    )

}