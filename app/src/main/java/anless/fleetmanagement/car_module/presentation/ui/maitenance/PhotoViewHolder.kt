package anless.fleetmanagement.car_module.presentation.ui.maitenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anless.fleetmanagement.databinding.ItemPhotoBinding
import com.bumptech.glide.Glide

class PhotoViewHolder private constructor(
    private val binding: ItemPhotoBinding,
    private val onClick: (PhotoItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClick: (PhotoItem) -> Unit): PhotoViewHolder {
            val binding =
                ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PhotoViewHolder(binding, onClick)
        }
    }

    private var item: PhotoItem? = null

    init {
        itemView.setOnClickListener {
            item?.let { photoItem ->
                onClick.invoke(photoItem)
            }
        }
    }

    fun bind(photoItem: PhotoItem) {
        item = photoItem

        Glide.with(itemView)
            .load(photoItem.uri)
            .into(binding.imgPhoto)

    }
}