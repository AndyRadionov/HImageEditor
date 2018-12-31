package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.model.entity.Picture

/**
 * @author Andrey Radionov
 */
interface EditorContract {

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun setPicture(photoPath: String)
        fun scaleImage(picturePath: String, height: Float)
        fun invertColors(picturePath: String, height: Float)
        fun flip(picturePath: String, height: Float)
        fun rotate(picturePath: String, height: Float)
    }

    interface View {
        fun onPictureChanged(picture: Picture)
        fun onTempPicturesChanged()
        fun initState(viewState: Pair<Picture?, ArrayList<Picture>>)
    }
}