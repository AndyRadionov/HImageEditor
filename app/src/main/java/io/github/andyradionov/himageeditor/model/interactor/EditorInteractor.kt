package io.github.andyradionov.himageeditor.model.interactor

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import io.github.andyradionov.himageeditor.model.data.PictureCache
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.utils.BitmapUtils
import io.github.andyradionov.himageeditor.model.utils.Loopers
import java.io.File

/**
 * @author Andrey Radionov
 */
class EditorInteractor (private val context: Context) {

    private val cache = PictureCache

    fun isCacheEmpty() = cache.isEmpty()

    fun getCache() = Pair(cache.getPicture(), cache.getTempPictures())

    fun createPicFile(): File? {
        return BitmapUtils.createTempImageFile(context)
    }

    fun scaleImage(picturePath: String, height: Float, pictureCallback: Callbacks.PicturesSingle) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                val bitmap = BitmapUtils.scalePic(context, picturePath, height)
                val smallPath = BitmapUtils.saveTempBitmap(context, bitmap)
                pictureCallback.onSuccess(Picture(picturePath, smallPath))
            }
        }.sendEmptyMessage(0)
    }

    fun removeTempPicture(photoPath: String) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                BitmapUtils.deleteImageFile(photoPath)
            }
        }.sendEmptyMessage(0)
    }
}
