package io.github.andyradionov.himageeditor.model.data

import io.github.andyradionov.himageeditor.model.entity.Picture

/**
 * @author Andrey Radionov
 */
object PictureCache {

    private var picture: Picture? = null
    private val tempPictures = ArrayList<Picture>()

    fun isEmpty() = picture == null

    fun getPicture() = picture

    fun getTempPictures() = tempPictures

    fun setPicture(picture: Picture) {
        this.picture = picture
    }

    fun addTempPicture(picture: Picture) {
        tempPictures.add(0, picture)
    }

    fun clearTempPictures() = tempPictures.clear()
}