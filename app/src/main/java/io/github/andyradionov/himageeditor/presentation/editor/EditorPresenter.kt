package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.interactor.Callbacks
import io.github.andyradionov.himageeditor.model.interactor.EditorInteractor

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

    override fun prepareCamera() {
        val file = editorInteractor.createPicFile()
        file?.let { view?.launchCamera(it) }
    }

    override fun removeTempPicture(photoPath: String) {
        editorInteractor.removeTempPicture(photoPath)
    }

    override fun preparePicture(photoPath: String) {

    }

    override fun setPicture(picture: Picture) {

    }

    override fun scaleImage(picturePath: String, height: Float) {
        editorInteractor.scaleImage(picturePath, height, object: Callbacks.PicturesSingle {
            override fun onSuccess(picture: Picture) {

            }
        })
    }

    override fun invertColors(picturePath: String, height: Float) {

    }

    override fun flip(picturePath: String, height: Float) {

    }

    override fun rotate(picturePath: String, height: Float) {

    }
}