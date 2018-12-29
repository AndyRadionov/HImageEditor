package io.github.andyradionov.himageeditor.utils

import android.content.Context
import android.preference.PreferenceManager
import io.github.andyradionov.himageeditor.R
import java.util.*
import kotlin.collections.HashSet

/**
 * @author Andrey Radionov
 */
object HistoryHelper {
    private const val HISTORY_LIST_SIZE = 100

    fun loadHistory(context: Context): MutableList<String> {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val defSet = HashSet<String>()
        val history = sharedPrefs.getStringSet(context.getString(R.string.history_key), defSet) as Set<String>
        val result = ArrayList(history)
        result.sortDescending()
        return result
    }

    fun updateHistoryList(context: Context, imgPath: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        var history = loadHistory(context)
        history.add(0, imgPath)

        history = if (history.size > HISTORY_LIST_SIZE)
            history.subList(0, HISTORY_LIST_SIZE) else history

        sharedPrefs.edit()
                .putStringSet(context.getString(R.string.history_key), HashSet(history))
                .apply()
    }
}
