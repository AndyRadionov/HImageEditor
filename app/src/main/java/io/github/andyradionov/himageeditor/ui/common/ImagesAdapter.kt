package io.github.andyradionov.himageeditor.ui.common

import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.model.utils.BitmapUtils
import kotlinx.android.synthetic.main.item_image.view.*


/**
 * @author Andrey Radionov
 */
class ImagesAdapter(
        private val imageClickListener: ImageClickListener?, private val pictures: List<Picture>
): RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    interface ImageClickListener {
        fun onClick(picture: Picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false) as CardView
        return ImageViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val picture = pictures[position]
        holder.itemView.resultPic.setImageURI(Uri.parse(picture.smallPath))
    }

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            imageClickListener?.onClick(pictures[adapterPosition])
        }
    }
}
