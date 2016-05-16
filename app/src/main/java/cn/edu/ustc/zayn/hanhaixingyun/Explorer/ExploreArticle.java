package cn.edu.ustc.zayn.hanhaixingyun.Explorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.zayn.hanhaixingyun.R;
import cn.edu.ustc.zayn.hanhaixingyun.adapter.ArticaleDetailsAdapter;
import cn.edu.ustc.zayn.hanhaixingyun.adapter.IndexListItemAdapter;
import cn.edu.ustc.zayn.hanhaixingyun.utils.CommonUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.HttpUtil;
import cz.msebera.android.httpclient.Header;

public class ExploreArticle extends AppCompatActivity {
    private Intent intent;
    private Article article;
    private static ArrayList<Article> articleList;
    private ArticaleDetailsAdapter articleDetailsAdapter;
    private TextView textView;
    private ListView listView;
    private String articleURL;
    private Document document;

    public class Article{
        public String articleBody, articleAuthor, articleDate;
        public boolean readed;
        public int linecount;
    }

    private void Inits(){
        textView = (TextView) findViewById(R.id.article_s_title);
        listView = (ListView) findViewById(R.id.article_contains);
        articleList = new ArrayList<>();
        intent = getIntent();
        articleURL = intent.getStringExtra("ArticleURL");
    }

    private void DealWithArticles(Document document){
        String title = String.valueOf(document.text());
        int i = title.indexOf("标题:")+4, j=title.indexOf(" ", i);
        textView.setText(title.substring(i, j));
        Elements articleBodys = document.select("div.post_text");
        for(i=0; i!=articleBodys.size(); i++){
            article = new Article();
            //body handling
            String ArticleBody = document.select("div.post_text").get(i).toString();
            int indexS = ArticleBody.indexOf("站内信件") + 15, indexE = ArticleBody.indexOf("<hr>");
            if(ArticleBody.contains("POST")){
                indexS += 16;
            }
            article.articleBody = ArticleBody.substring(indexS).replace(".einfo{height:", "");
            if(document.select("table.attachment").size() > i) {
                String attachment = document.select("table.attachment").get(i).toString();
                article.articleBody += attachment;
            }
            //author info
            indexS = ArticleBody.indexOf("发信人:&nbsp;")+10;
            indexE = ArticleBody.indexOf("&", indexS);
            article.articleAuthor = ArticleBody.substring(indexS, indexE);
            //date info
            indexS = ArticleBody.indexOf("发信站:&nbsp;瀚海星云&nbsp;(")+21;
            indexE = ArticleBody.indexOf(")", indexS);
            article.articleDate = ArticleBody.substring(indexS, indexE).replace("&nbsp;", " ");
            //add
            article.readed = false;
            article.linecount = -1;
            articleList.add(article);
        }
        articleDetailsAdapter = new ArticaleDetailsAdapter(getApplicationContext(), articleList);
        listView.setAdapter(articleDetailsAdapter);
    }

    private void ParseIndexPage() {
        //HttpUtil.getClient().addHeader("Cookie", "m=1;my_t_lines=; my_link_mode=; my_def_mode=" + "; utmpnum=" + utmpnum
        //        + "; utmpkey=" + utmpkey + "; utmpuserid=" + utmpuserid);
        HttpUtil.getClient().addHeader("Upgrade-Insecure-Requests", "1");
        RequestParams params = new RequestParams();
        HttpUtil.getClient().setURLEncodingEnabled(true);
        HttpUtil.post(HttpUtil.URLReasources + articleURL, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String body = new String(responseBody, "GBK");
                            document = Jsoup.parse(body, "GBK");
                            //textView.setText(Html.fromHtml(articleBody));
                            DealWithArticles(document);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_article);
        Inits();
        ParseIndexPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_explore_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
