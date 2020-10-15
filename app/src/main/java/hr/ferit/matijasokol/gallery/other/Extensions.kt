package hr.ferit.matijasokol.gallery.other

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import hr.ferit.matijasokol.gallery.R

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(R.drawable.ic_error)
        .into(this)
}

fun ImageView.loadImageWithCallbacks(url: String, fragment: Fragment, onLoadFailed: () -> Boolean, onResourceReady: () -> Boolean) {
    Glide.with(fragment)
        .load(url)
        .error(R.drawable.ic_error)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return onLoadFailed.invoke()
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return onResourceReady.invoke()
            }
        })
        .into(this)
}