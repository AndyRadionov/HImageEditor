package io.github.andyradionov.himageeditor.model.interactor

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import io.github.andyradionov.himageeditor.model.data.PictureCache
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.utils.BitmapUtils
import io.github.andyradionov.himageeditor.model.utils.HistoryHelper
import io.github.andyradionov.himageeditor.model.utils.Loopers
import java.io.File

/**
 * @author Andrey Radionov
 */
class EditorInteractor(private val context: Context) {

    private val cache = PictureCache

    fun isCacheEmpty() = cache.isEmpty()

    fun getCache() = Pair(cache.getPicture(), cache.getTempPictures())

    fun createPicFile(callback: Callbacks.FileSingle) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                val file = BitmapUtils.createTempImageFile(context)
                callback.onSuccess(file)
            }
        }.sendEmptyMessage(0)
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

    fun preparePicture(photoPath: String, imgHeightDp: Float, callback: Callbacks.PicturesSingle) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                val bitmap = BitmapUtils.scalePic(context, photoPath, imgHeightDp)
                val smallPath = BitmapUtils.saveTempBitmap(context, bitmap)
                callback.onSuccess(Picture(photoPath, smallPath))
            }
        }.sendEmptyMessage(0)
    }

    fun savePicture(operation: Callbacks.Operation) {
        if (cache.isEmpty()) {
            operation.onFail()
        } else {
            object : Handler(Loopers.backgroundLooper) {
                override fun handleMessage(msg: Message?) {
                    val photoPath = cache.getPicture()?.fullPath
                    val saveBitmap = BitmapUtils.resamplePic(context, photoPath)
                    BitmapUtils.deleteImageFile(photoPath)
                    BitmapUtils.deleteTempFiles(cache.getTempPictures())
                    cache.clear()
                    BitmapUtils.saveImage(context, saveBitmap)
                    HistoryHelper.updateHistoryList(context, cache.getPicture()!!)
                    operation.onSuccess()
                }
            }.sendEmptyMessage(0)
        }
    }
}
