package cn.edu.ustc.zayn.hanhaixingyun;

import android.os.Looper;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.ustc.zayn.hanhaixingyun.utils.HttpUtil;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zaynr on 2016/5/13.
 */
public class Test {
    private static String board = "Notice";
    private static List<String> listItem = new ArrayList<>();
    public static void main(String[] argc){
        try{
            Document document;
                Connection.Response response = Jsoup.connect("http://bbs.ustc.edu.cn/cgi/bbsindex")
                        .data("id", HttpUtil.id, "pw", HttpUtil.password, "x", HttpUtil.x, "y", HttpUtil.y)
                        .method(Connection.Method.POST)
                        .execute();
                document = response.parse();
            System.out.println(document.body());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}