package com.architecture.realarchitecture.datasource.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyManager {

    //请求队列
    private RequestQueue mRequestQueue;
    //图片缓存加载器
    private ImageLoader mImageLoader;
    //上下文对象
    private static Context mContext;

    private static VolleyManager mInstance;


    private VolleyManager(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new LruBitmapCache(context));
    }

    /**
     * 获取VolleyManager单例
     *
     * @param context c
     * @return instance
     */
    public static synchronized VolleyManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyManager(context);
        }
        return mInstance;
    }

    /**
     * 返回一个Volley网络请求队列
     *
     * @return RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            } else {
                mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new OkHttpStack());
            }
            mRequestQueue.start();
        }

        return mRequestQueue;

    }

    /**
     * 添加一个request到请求队列
     *
     * @param req req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * 获取一个图片缓存容器
     *
     * @return imageLoader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

//    /**
//     *
//     */
//    private static class OkHttpStack extends HurlStack {
//        private final OkUrlFactory okUrlFactory;
//
//        public OkHttpStack() {
//            this(new OkUrlFactory(new OkHttpClient()));
//        }
//
//        public OkHttpStack(OkUrlFactory okUrlFactory) {
//            if (okUrlFactory == null) {
//                throw new NullPointerException("Client must not be null.");
//            }
//            this.okUrlFactory = okUrlFactory;
////            this.okUrlFactory.client().networkInterceptors().add(new StethoInterceptor());
//        }
//
//        @Override
//        protected HttpURLConnection createConnection(URL url) throws IOException {
//            return okUrlFactory.open(url);
//        }
//    }


    /**
     *
     */
    public static class LruBitmapCache extends LruCache<String, Bitmap>
            implements ImageLoader.ImageCache {

        public LruBitmapCache(int maxSize) {
            super(maxSize);
        }

        public LruBitmapCache(Context ctx) {
            this(getCacheSize(ctx));
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

        // Returns a cache size equal to approximately three screens worth of images.
        private static int getCacheSize(Context ctx) {
            /**
             * 1/8
             */
            final int maxMemory = (int) Runtime.getRuntime().maxMemory();
            final int cacheSize = maxMemory / 8;

            /**
             * 3屏幕
             */
            return cacheSize;
        }
    }

}
