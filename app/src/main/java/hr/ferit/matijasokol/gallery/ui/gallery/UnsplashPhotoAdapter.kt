package hr.ferit.matijasokol.gallery.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.gallery.models.UnsplashPhoto
import hr.ferit.matijasokol.gallery.databinding.ItemUnsplashPhotoBinding
import hr.ferit.matijasokol.gallery.other.loadImage

class UnsplashPhotoAdapter(private val onItemClicked: (UnsplashPhoto) -> Unit) : PagingDataAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {

            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder = PhotoViewHolder(
        ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let { onItemClicked.invoke(it) }
                }
            }
        }

        fun bind(photo: UnsplashPhoto) {
            with(binding) {
                ivImage.loadImage(photo.urls.regular)
                tvUsername.text = photo.user.username
            }
        }
    }
}