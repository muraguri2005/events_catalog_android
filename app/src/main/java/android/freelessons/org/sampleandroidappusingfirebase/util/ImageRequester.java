package android.freelessons.org.sampleandroidappusingfirebase.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

/**
 * Created by richard on 24/07/2017.
 */

public class ImageRequester {
    private ImageLoader imageLoader;
    private int maxByteSize;
    private ImageRequester(Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.start();
        this.maxByteSize = calculateMaxByteSize(context);
        this.imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String,Bitmap> lruCache =  new LruCache(maxByteSize){
                @Override
                protected int sizeOf(Object key, Object value) {
                    return ((Bitmap)value).getByteCount();
                }
            };
            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url,bitmap);
            }
        });
    }
    public void setImageFromUrl(NetworkImageView networkImageView,String url) {
        networkImageView.setImageUrl(url,imageLoader);
    }
    private int calculateMaxByteSize(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4;
        return screenBytes * 3;
    }
    private static ImageRequester instance = null;
   public static ImageRequester getInstance(Context context) {
        ImageRequester result = instance;
        if (result == null) {
            synchronized (ImageRequester.class) {
                result = instance;
                if (result == null) {
                    instance = new ImageRequester(context);
                    result = instance;
                }
            }
        }
        return result;
    }
}
