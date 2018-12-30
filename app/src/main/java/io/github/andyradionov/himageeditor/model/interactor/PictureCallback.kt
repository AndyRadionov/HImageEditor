package io.github.andyradionov.himageeditor.model.interactor

import io.github.andyradionov.himageeditor.model.entity.Picture

/**
 * @author Andrey Radionov
 */
interface PictureCallback {
    fun onSuccess(picture: Picture)
}