package io.github.andyradionov.himageeditor.model.interactor

import android.content.Context
import android.graphics.Bitmap
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
                val picture = Picture(photoPath, smallPath)
                cache.setPicture(picture)
                callback.onSuccess(picture)
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
                    BitmapUtils.saveImage(context, saveBitmap)
                    HistoryHelper.updateHistoryList(context, cache.getPicture()!!)
                    cache.clear()
                    operation.onSuccess()
                }
            }.sendEmptyMessage(0)
        }
    }

    fun setPicture(picture: Picture) {
        cache.setPicture(picture)
        cache.removeSettedPicture(picture)
    }

    fun invert(height: Float, callback: Callbacks.Operation) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                convert(height, callback) { bitmap -> BitmapUtils.invertColors(bitmap) }
            }
        }.sendEmptyMessage(0)
    }

    fun flip(height: Float, callback: Callbacks.Operation) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                convert(height, callback) { bitmap -> BitmapUtils.flip(bitmap) }
            }
        }.sendEmptyMessage(0)
    }

    fun rotate(height: Float, callback: Callbacks.Operation) {
        object : Handler(Loopers.backgroundLooper) {
            override fun handleMessage(msg: Message?) {
                convert(height, callback) { bitmap -> BitmapUtils.rotate(bitmap) }
            }
        }.sendEmptyMessage(0)
    }

    fun clear() {
        cache.clear()
    }

    private fun convert(height: Float, callback: Callbacks.Operation, operation: (bitmap: Bitmap) -> Bitmap) {
        val bitmap = BitmapUtils.scalePic(context, cache.getPicture()?.fullPath, height)
        val convert = operation(bitmap)
        val fullPath = BitmapUtils.saveTempBitmap(context, convert)
        val smallBitmap = BitmapUtils.scalePic(context, fullPath, height)
        val smallPath = BitmapUtils.saveTempBitmap(context, smallBitmap)
        cache.addTempPicture(Picture(fullPath, smallPath))
        callback.onSuccess()
    }
}
