package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.interactor.EditorInteractor
import io.github.andyradionov.himageeditor.model.interactor.PictureCallback

/**
 * @author Andrey Radionov
 */
class EditorPresenter(
        private val editorInteractor: EditorInteractor
) : EditorContract.Presenter {

    private var view: EditorContract.View? = null

    override fun attachView(view: EditorContract.View) {
        this.view = view
        val viewState = if (!editorInteractor.isCacheEmpty()) {
            editorInteractor.getCache()
        } else null to editorInteractor.getCache().second
        view.initState(viewState)
    }

    override fun detachView() {
        this.view = null
    }

    override fun setPicture(photoPath: String) {

    }

    override fun scaleImage(picturePath: String, height: Float) {
        editorInteractor.scaleImage(picturePath, height, object: PictureCallback {
            override fun onSuccess(picture: Picture) {

            }

        })
    }

    override fun invertColors(picturePath: String, height: Float) {

    }

    override fun flip(picturePath: String, height: Float) {}

    override fun rotate(picturePath: String, height: Float) {}
}