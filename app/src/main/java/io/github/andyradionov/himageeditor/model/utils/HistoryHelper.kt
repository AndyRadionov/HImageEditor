package io.github.andyradionov.himageeditor.model.utils

import android.content.Context
import android.preference.PreferenceManager
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.model.entity.Picture
import java.util.*
import kotlin.collections.HashSet

/**
 * @author Andrey Radionov
 */
object HistoryHelper {
    private const val HISTORY_LIST_SIZE = 100
    private const val SMALL_PATH = "%s_small"

    fun loadHistory(context: Context): MutableList<Picture> {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val defSet = HashSet<String>()
        val history = sharedPrefs.getStringSet(context.getString(R.string.history_key), defSet) as Set<String>
        val result = history.map { s -> Picture(s,String.format(SMALL_PATH, s)) }.toMutableList()
        result.sortDescending()
        return result
    }

    fun updateHistoryList(context: Context, picture: Picture) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        var history = loadHistory(context)
        history.add(0, picture)

        history = if (history.size > HISTORY_LIST_SIZE)
            history.subList(0, HISTORY_LIST_SIZE) else history

        val historyToSave = history.map { p -> p.fullPath }.toSet()
        sharedPrefs.edit()
                .putStringSet(context.getString(R.string.history_key), historyToSave)
                .apply()
    }
}
