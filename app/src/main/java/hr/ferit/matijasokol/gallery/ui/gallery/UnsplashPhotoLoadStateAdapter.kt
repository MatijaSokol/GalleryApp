package hr.ferit.matijasokol.gallery.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.gallery.databinding.UnsplashPhotoLoadStateFooterBinding

class UnsplashPhotoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<UnsplashPhotoLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = LoadStateViewHolder(
        UnsplashPhotoLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bind(loadState)

    inner class LoadStateViewHolder(
        private val binding: UnsplashPhotoLoadStateFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                progressBar.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState !is LoadState.Loading
                tvError.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}