package io.github.andyradionov.himageeditor.model.utils

import android.os.HandlerThread
import android.os.Looper

/**
 * @author Andrey Radionov
 */
object Loopers {
    val backgroundLooper: Looper
    val uiLooper = Looper.getMainLooper()
    init {
        val backgroundThread = HandlerThread("backgroundThread")
        backgroundThread.start()
        backgroundLooper = backgroundThread.looper
    }
}