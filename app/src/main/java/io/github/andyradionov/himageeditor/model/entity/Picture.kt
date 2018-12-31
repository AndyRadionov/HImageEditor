package io.github.andyradionov.himageeditor.model.entity

/**
 * @author Andrey Radionov
 */
data class Picture (val fullPath: String, val smallPath: String) : Comparable<Picture> {
    override fun compareTo(other: Picture): Int {
        return this.fullPath.compareTo(other.fullPath)
    }
}
