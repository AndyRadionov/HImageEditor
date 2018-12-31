package io.github.andyradionov.himageeditor.presentation.history

import android.view.View
import io.github.andyradionov.himageeditor.model.entity.Picture

/**
 * @author Andrey Radionov
 */
interface HistoryContract {

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
    }

    interface View {
        fun showHistory(pictures: List<Picture>)
    }
}