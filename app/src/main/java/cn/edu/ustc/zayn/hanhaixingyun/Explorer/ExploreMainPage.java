package cn.edu.ustc.zayn.hanhaixingyun.Explorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.ustc.zayn.hanhaixingyun.R;
import cn.edu.ustc.zayn.hanhaixingyun.adapter.IndexListItemAdapter;
import cn.edu.ustc.zayn.hanhaixingyun.utils.CommonUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.HttpUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.SharedPreferenceUtil;
import cz.msebera.android.httpclient.Header;

public class ExploreMainPage extends AppCompatActivity{

    //variables
    private String utmpnum, utmpuserid, utmpkey;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ListView listView;
    private String board;
    private Document document;
    private ArrayList<ListItem> listItems;
    private IndexListItemAdapter indexListItemAdapter;
    private Toolbar toolbar;

    public class ListItem{
        public String title, articleURL, author, status, date, reply, iconURL;
    }

    private void InitCookie(){
        if(sharedPreferenceUtil.getKeyData("isLogged in") == "TRUE"){
            utmpnum = sharedPreferenceUtil.getKeyData("utmpnum");
            utmpuserid = sharedPreferenceUtil.getKeyData("utmpuserid");
            utmpkey = sharedPreferenceUtil.getKeyData("utmpkey");
        }
        else{
            utmpnum = "";
            utmpuserid = "";
            utmpkey = "";
        }
    }

    private void ParseIndexPage() {
        final ProgressDialog progressDialog = CommonUtil.getProcessDialog(this, "Parsing...");
        progressDialog.show();
        HttpUtil.getClient().addHeader("Cookie", "m=1;my_t_lines=; my_link_mode=; my_def_mode=" + "; utmpnum=" + utmpnum
                + "; utmpkey=" + utmpkey + "; utmpuserid=" + utmpuserid);
        HttpUtil.getClient().addHeader("Upgrade-Insecure-Requests", "1");
        RequestParams params = new RequestParams();
        HttpUtil.getClient().setURLEncodingEnabled(true);
        HttpUtil.post(HttpUtil.URLBoard + board, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String body = new String(responseBody, "GBK");
                            document = Jsoup.parse(body, "GBK");
                            Elements titles = document.select("td.title");
                            Elements status = document.select("td.status");
                            Elements authors = document.select("td.author");
                            Elements dates = document.select("td.datetime");
                            Elements replys = document.select("td.hot");
                            Elements urls = titles.select("a[href]");
                            for (int i = 0; i != titles.size(); i++) {
                                ListItem item = new ListItem();
                                item.title = titles.get(i).text();
                                item.status = status.get(i).text();
                                item.author = authors.get(i).text();
                                item.date = dates.get(i).text();
                                item.reply = replys.get(i).text();
                                item.articleURL = urls.get(i).attr("href");
                                listItems.add(item);
                            }
                            indexListItemAdapter
                                    = new IndexListItemAdapter(getApplicationContext(), listItems);
                            listView.setAdapter(indexListItemAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(),
                                "网络连接存在问题！", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();
    }

    private void InitEvent(){
        listItems = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_item);
        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ExploreMainPage.this, ExploreArticle.class);
                intent.putExtra("ArticleURL", listItems.get(position).articleURL);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_explore_main_page);
        //ButterKnife.bind(this);
        this.setSupportActionBar(this.toolbar);
        sharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext(), "accountInfo");
        InitCookie();
        InitEvent();
        board = "piebridge";
        ParseIndexPage();

    }
}
