package cn.edu.ustc.zayn.hanhaixingyun.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by zaynr on 2016/5/15.
 */
public class URLImageParser implements Html.ImageGetter {
    Context c;
    View container;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public URLImageParser(View t, Context c) {
        this.c = c;
        this.container = t;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();
        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);
        asyncTask.execute(source);
        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;
        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }
        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }
        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
            container.measure(0, 0);
            int scale = container.getWidth();
            urlDrawable.setBounds(0, 0, scale, ((int)Math.ceil((double)scale/result.getIntrinsicWidth()))*result.getIntrinsicHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();
        }
        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                container.measure(0, 0);
                int scale = container.getWidth();
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, scale, ((int)Math.ceil((double)scale/drawable.getIntrinsicWidth()))*drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }
        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            int i = urlString.indexOf("an=")+3, j=urlString.indexOf(".");
            String chinese = urlString.substring(i,j),
                    chineseEncode = URLEncoder.encode(chinese, "gb2312");
            urlString = urlString.replace(chinese, chineseEncode);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(HttpUtil.URLReasources + urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}
