package io.github.andyradionov.himageeditor.ui

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.utils.BitmapUtils
import kotlinx.android.synthetic.main.item_image.view.*


/**
 * @author Andrey Radionov
 */
class ImagesAdapter(
        private val imageClickListener: ImageClickListener, private val images: List<String>
): RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    interface ImageClickListener {
        fun onClick(imagePath: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false) as CardView
        return ImageViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imgPath = images[position]
        //holder.itemView.resultPic.setImageURI(Uri.parse(imgPath))
        val bitmap = BitmapUtils.scalePic(holder.itemView.context, imgPath, 160f)
        holder.itemView.resultPic.setImageBitmap(bitmap)
    }

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            imageClickListener.onClick(images[adapterPosition])
        }
    }
}
