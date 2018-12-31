package io.github.andyradionov.himageeditor.model.interactor

import io.github.andyradionov.himageeditor.model.entity.Picture

/**
 * @author Andrey Radionov
 */
interface Callbacks {
    interface PicturesSingle {
        fun onSuccess(picture: Picture)
    }

    interface PicturesList {
        fun onSuccess(pictures: List<Picture>)
    }
}