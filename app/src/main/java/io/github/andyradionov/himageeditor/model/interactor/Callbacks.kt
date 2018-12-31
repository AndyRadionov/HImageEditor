package io.github.andyradionov.himageeditor.model.interactor

import io.github.andyradionov.himageeditor.model.entity.Picture
import java.io.File

/**
 * @author Andrey Radionov
 */
interface Callbacks {

    interface Operation {
        fun onSuccess()
        fun onFail()
    }

    interface FileSingle {
        fun onSuccess(file: File)
    }

    interface PicturesSingle {
        fun onSuccess(picture: Picture)
    }

    interface PicturesList {
        fun onSuccess(pictures: List<Picture>)
    }
}