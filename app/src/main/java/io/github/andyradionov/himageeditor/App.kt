package io.github.andyradionov.himageeditor

import android.app.Application
import io.github.andyradionov.himageeditor.model.interactor.EditorInteractor
import io.github.andyradionov.himageeditor.model.interactor.HistoryInteractor
import io.github.andyradionov.himageeditor.presentation.editor.EditorContract
import io.github.andyradionov.himageeditor.presentation.editor.EditorPresenter
import io.github.andyradionov.himageeditor.presentation.history.HistoryContract
import io.github.andyradionov.himageeditor.presentation.history.HistoryPresenter

/**
 * @author Andrey Radionov
 */
class App : Application() {

    companion object {
        lateinit var editorPresenter: EditorContract.Presenter
        lateinit var historyPresenter: HistoryContract.Presenter
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        editorPresenter = EditorPresenter(EditorInteractor(this))
        historyPresenter = HistoryPresenter(HistoryInteractor(this))
    }
}
