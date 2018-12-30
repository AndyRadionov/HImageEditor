package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.model.interactor.EditorInteractor

/**
 * @author Andrey Radionov
 */
class EditorPresenter(
        private val editorInteractor: EditorInteractor
) : EditorContract.Presenter {
}