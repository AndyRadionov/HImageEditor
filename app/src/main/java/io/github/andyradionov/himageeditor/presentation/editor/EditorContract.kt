package io.github.andyradionov.himageeditor.presentation.editor

import io.github.andyradionov.himageeditor.model.entity.Picture
import java.io.File

/**
 * @author Andrey Radionov
 */
interface EditorContract {

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun preparePicture(photoPath: String, height: Float)
        fun setPicture(picture: Picture)
        fun scaleImage(picturePath: String, height: Float)
        fun invertColors(picturePath: String, height: Float)
        fun flip(picturePath: String, height: Float)
        fun rotate(picturePath: String, height: Float)
        fun prepareCamera()
        fun removeTempPicture(photoPath: String)
        fun savePicture()
    }

    interface View {
        fun launchCamera(file: File)
        fun onPictureChanged(picture: Picture)
        fun onTempPicturesChanged()
        fun initState(viewState: Pair<Picture?, ArrayList<Picture>>)
        fun showMsg(msgId: Int)
    }
}