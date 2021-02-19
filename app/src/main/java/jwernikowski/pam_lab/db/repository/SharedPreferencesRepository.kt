package jwernikowski.pam_lab.db.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesRepository(context: Context) {

    companion object {
        val PREFERENCES_NAME = "practice_assistant_prefs"
        val PREVIOUS_TEMPO = "previous_tempo"
        val PREVIOUS_RHYTHM = "previous_rhythm"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var previousTempo: Int?
    get() {
        return if (preferences.contains(PREVIOUS_TEMPO)) {
            preferences.getInt(PREVIOUS_TEMPO, 0)
        } else null
    }
    set(value) {
        value?.let { preferences.edit().putInt(PREVIOUS_TEMPO, it).apply() } ?:
                preferences.edit().remove(PREVIOUS_TEMPO).apply()
    }

    var previousRhythmId: Long?
    get() {
        return if (preferences.contains(PREVIOUS_RHYTHM)) {
            preferences.getLong(PREVIOUS_RHYTHM, 0L)
        } else null
    }
    set(value) {
        value?.let { preferences.edit().putLong(PREVIOUS_RHYTHM, it).apply() } ?:
                preferences.edit().remove(PREVIOUS_RHYTHM).apply()
    }

}