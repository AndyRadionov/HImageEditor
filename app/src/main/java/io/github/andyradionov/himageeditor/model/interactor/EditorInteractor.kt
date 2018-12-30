package io.github.andyradionov.himageeditor.model.interactor

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.utils.BitmapUtils
import io.github.andyradionov.himageeditor.model.utils.Loopers

/**
 * @author Andrey Radionov
 */
class EditorInteractor (private val context: Context) {

    private var picturePath: String? = null
    private val tempPictures = ArrayList<String>()
    private var handler: Handler? = null

    fun scaleImage(picturePath: String, height: Float, pictureCallback: PictureCallback) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                val bitmap = BitmapUtils.scalePic(context, picturePath, height)
                val smallPath = BitmapUtils.saveTempBitmap(context, bitmap)

                pictureCallback.onSuccess(Picture(picturePath, smallPath))
            }
        }.sendEmptyMessage(0)
    }
}