package io.github.andyradionov.himageeditor.presentation.history

import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.interactor.Callbacks
import io.github.andyradionov.himageeditor.model.interactor.HistoryInteractor

/**
 * @author Andrey Radionov
 */
class HistoryPresenter(
        private val historyInteractor: HistoryInteractor
) : HistoryContract.Presenter {

    private var view: HistoryContract.View? = null

    override fun attachView(view: HistoryContract.View) {
        this.view = view
        historyInteractor.loadHistory(object : Callbacks.PicturesList {
            override fun onSuccess(pictures: List<Picture>) {
                this@HistoryPresenter.view?.showHistory(pictures)
            }
        })
    }

    override fun detachView() {
        this.view = null
    }
}