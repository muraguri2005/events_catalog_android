package android.freelessons.org.sampleandroidappusingfirebase.util

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley

/**
 * Created by richard on 24/07/2017.
 */
class ImageRequester private constructor(context: Context) {
    private val imageLoader: ImageLoader
    private val maxByteSize: Int
    fun setImageFromUrl(networkImageView: NetworkImageView, url: String?) {
        networkImageView.setImageUrl(url, imageLoader)
    }

    private fun calculateMaxByteSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        return screenBytes * 3
    }

    companion object {
        private var instance: ImageRequester? = null
        @JvmStatic
        fun getInstance(context: Context): ImageRequester? {
            var result = instance
            if (result == null) {
                synchronized(ImageRequester::class.java) {
                    result = instance
                    if (result == null) {
                        instance = ImageRequester(context)
                        result = instance
                    }
                }
            }
            return result
        }
    }

    init {
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.start()
        maxByteSize = calculateMaxByteSize(context)
        imageLoader = ImageLoader(requestQueue, object : ImageLoader.ImageCache {
            private val lruCache = object : LruCache<String?, Bitmap>(maxByteSize) {
                override fun sizeOf(key: String?, value: Bitmap): Int {
                    return value.byteCount
                }
            }

            override fun getBitmap(url: String): Bitmap? {
                return lruCache[url]
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                lruCache.put(url, bitmap)
            }
        })
    }
}