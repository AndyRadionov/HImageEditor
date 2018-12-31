package io.github.andyradionov.himageeditor.model.interactor

import android.content.Context
import android.os.Handler
import android.os.Message
import io.github.andyradionov.himageeditor.model.utils.HistoryHelper
import io.github.andyradionov.himageeditor.model.utils.Loopers

/**
 * @author Andrey Radionov
 */
class HistoryInteractor(private val context: Context) {

    fun loadHistory(callback: Callbacks.PicturesList) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {

                val history = HistoryHelper.loadHistory(context)
                callback.onSuccess(history)
            }
        }.sendEmptyMessage(0)
    }
}