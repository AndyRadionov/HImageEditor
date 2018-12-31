package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.interactor.Callbacks
import io.github.andyradionov.himageeditor.model.interactor.EditorInteractor
import java.io.File

/**
 * @author Andrey Radionov
 */
class EditorPresenter(
        private val editorInteractor: EditorInteractor
) : EditorContract.Presenter {

    private var view: EditorContract.View? = null

    override fun attachView(view: EditorContract.View) {
        this.view = view
        view.initState(getViewState())
    }

    override fun detachView() {
        this.view = null
    }

    override fun prepareCamera() {
        editorInteractor.createPicFile(object : Callbacks.FileSingle {
            override fun onSuccess(file: File) {
                view?.launchCamera(file)
            }
        })
    }

    override fun removeTempPicture(photoPath: String) {
        editorInteractor.removeTempPicture(photoPath)
    }

    override fun preparePicture(photoPath: String, height: Float) {
        editorInteractor.preparePicture(photoPath, height, object : Callbacks.PicturesSingle {
            override fun onSuccess(picture: Picture) {
                view?.onPictureChanged(picture)
            }
        })
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

    override fun savePicture() {
        editorInteractor.savePicture(object: Callbacks.Operation{
            override fun onFail() {
                view?.showMsg(R.string.error_message)
            }

            override fun onSuccess() {
                view?.showMsg(R.string.saved_message)
                view?.initState(getViewState())
            }
        })
    }

    private fun getViewState(): Pair<Picture?, ArrayList<Picture>> {
        return if (!editorInteractor.isCacheEmpty()) {
            editorInteractor.getCache()
        } else null to editorInteractor.getCache().second
    }
}