package cn.edu.ustc.zayn.hanhaixingyun.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zaynr on 2016/5/10.
 * Http tools
 */
public class HttpUtil {
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    //URLs
    public static final String HOST = "bbs.ustc.edu.cn",
            URLLogin = "http://bbs.ustc.edu.cn/cgi/bbslogin",
            URLCheckBox = "";
    public static String URLMain = "http://bbs.ustc.edu.cn/cgi/bbsindex",
            URLBoard = "http://bbs.ustc.edu.cn/cgi/bbstdoc?board=",
            URLQueryID = "http://bbs.ustc.edu.cn/cgi/bbsqry?userid=",
            URLReasources = "http://bbs.ustc.edu.cn/cgi/";
            //URLQuery = "http://bbs.ustc.edu.cn/bbstty";
    //post vars
    public static String id = "guest",
            password = "aabb",
            x = "1",
            y = "1";
    // init client properties
    static {
        asyncHttpClient.setTimeout(5000);// 5s, default as 10s
        //set headers
        asyncHttpClient.addHeader("Host", HOST);
        asyncHttpClient.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
        asyncHttpClient.addHeader("Connection", "Keep-Alive");
    }


    /**
     * get specific board URL
     *
     * @param board
     * @return
     */
    public String getURLBoard(String board){
        return URLBoard + board;
    }

    /**
     * get,用一个完整url获取一个string对象
     *
     * @param urlString
     * @param res
     */
    public static void get(String urlString, AsyncHttpResponseHandler res) {
        asyncHttpClient.get(urlString, res);
    }

    /**
     * get,url里面带参数
     *
     * @param urlString
     * @param params
     * @param res
     */
    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler res) {
        asyncHttpClient.get(urlString, params, res);
    }

    /**
     * get,下载数据使用，会返回byte数据
     *
     * @param uString
     * @param bHandler
     */
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {
        asyncHttpClient.get(uString, bHandler);
    }

    /**
     * post,不带参数
     *
     * @param urlString
     * @param res
     */
    public static void post(String urlString, AsyncHttpResponseHandler res) {
        asyncHttpClient.post(urlString, res);
    }

    /**
     * post,带参数
     *
     * @param urlString
     * @param params
     * @param res
     */
    public static void post(String urlString, RequestParams params,
                            AsyncHttpResponseHandler res) {
        asyncHttpClient.post(urlString, params, res);
    }

    /**
     * post,返回二进制数据时使用，会返回byte数据
     *
     * @param uString
     * @param bHandler
     */
    public static void post(String uString, BinaryHttpResponseHandler bHandler) {
        asyncHttpClient.post(uString, bHandler);
    }

    /**
     * 返回请求客户端
     *
     * @return
     */
    public static AsyncHttpClient getClient() {
        return asyncHttpClient;
    }

    /**
     * set params for login
     *
     * @return
     */
    public static RequestParams getLoginParams(){
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("pw", password);
        requestParams.add("x", x);
        requestParams.add("y", y);
        return requestParams;
    }

    public interface QueryCallback {
        public String handleResult(byte result[]);
    }

}
