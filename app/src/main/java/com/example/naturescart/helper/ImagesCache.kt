package com.example.naturescart.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition


open class ImagesCache {

    companion object {
        const val cacheSize = 12 * 1024 * 1024 // 12MiB
        private var memCache: LruCache<String?, Drawable?>? = null
    }

    init {
        if (memCache == null) {
            memCache = object : LruCache<String?, Drawable?>(cacheSize) {
                protected fun sizeOf(key: String?, image: Bitmap): Int {
                    return image.byteCount / 1024
                }
            }
        }
    }

    fun loadImage(context: Context, image: String, onLoadCompleted: () -> Unit) {
        Glide.with(context).load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    memCache?.put(image, resource)
                    onLoadCompleted()
                    Log.d("TAGEE", "Loaded")
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    onLoadCompleted()
                    Log.d("TAGEE", "Failed")
                    return false
                }
            }).preload()
    }

    fun getImage(imageUrl: String): Drawable? {
        return memCache?.get(imageUrl)
    }
}