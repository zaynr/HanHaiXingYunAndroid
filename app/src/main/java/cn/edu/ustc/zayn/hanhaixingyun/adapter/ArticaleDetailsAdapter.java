package cn.edu.ustc.zayn.hanhaixingyun.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.zayn.hanhaixingyun.Explorer.ExploreArticle;
import cn.edu.ustc.zayn.hanhaixingyun.Explorer.ExploreMainPage;
import cn.edu.ustc.zayn.hanhaixingyun.R;
import cn.edu.ustc.zayn.hanhaixingyun.utils.DownloadImageTask;
import cn.edu.ustc.zayn.hanhaixingyun.utils.HttpUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.URLImageParser;
import cz.msebera.android.httpclient.Header;

/**
 * Created by zaynr on 2016/5/14.
 */
public class ArticaleDetailsAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ExploreArticle.Article> articles;
    private ViewHolder viewHolder;
    private List<Articleline> lines;

    public class Articleline{
        int linecount;
    }

    private class ViewHolder{
        public Button readMore;
        public ImageView icon;
        public TextView date, author, body;
        public int pos;
    }

    public ArticaleDetailsAdapter(Context context, ArrayList < ExploreArticle.Article > articles){
        super();
        lines=new ArrayList<>();
        this.articles = articles;
        this.context = context;
    }

    public ArrayList<ExploreArticle.Article> getArticles(){
        return articles;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.article_page_details, null);
            viewHolder.readMore = (Button) convertView.findViewById(R.id.show_more);
            viewHolder.body = (TextView) convertView.findViewById(R.id.article_details_body);
            viewHolder.author = (TextView) convertView.findViewById(R.id.article_details_author);
            viewHolder.date = (TextView) convertView.findViewById(R.id.article_details_date);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.article_details_icon);
            viewHolder.icon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_dialog_email));
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final View view = convertView;
        viewHolder.pos = position;
        ExploreArticle.Article article = articles.get(position);
        //viewHolder.body.setText(Html.fromHtml(article.articleBody));
        viewHolder.body = (TextView)convertView.findViewById(R.id.article_details_body);
        URLImageParser p = new URLImageParser(viewHolder.body, context);
        Spanned htmlSpan = Html.fromHtml(article.articleBody, p, null);
        viewHolder.body.setText(htmlSpan);

        final int pos = position;
        //viewHolder.body.setLines(5);
        viewHolder.date.setText(article.articleDate);
        viewHolder.author.setText(article.articleAuthor);
        class GetLineCount extends AsyncTask<Integer, Integer, String>{
            private ViewHolder viewHolder;

            public GetLineCount(ViewHolder viewHolder){
                this.viewHolder = viewHolder;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String params) {
                super.onPostExecute(params);
                int linecount = viewHolder.body.getLineCount();
                if(linecount > 10 && String.valueOf(viewHolder.readMore.getText()).equals("阅读全文")){
                    viewHolder.body.setMaxLines(10);
                    viewHolder.readMore.setVisibility(View.VISIBLE);
                }
                else if(!String.valueOf(viewHolder.readMore.getText()).equals("收起")){
                    viewHolder.readMore.setVisibility(View.GONE);
                }
                viewHolder.readMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(String.valueOf(viewHolder.readMore.getText()).equals("阅读全文")){
                            viewHolder.body.setMaxLines(300);
                            viewHolder.readMore.setText("收起");
                        }
                        else{
                            viewHolder.body.setMaxLines(10);
                            viewHolder.readMore.setText("阅读全文");
                        }
                    }
                });
            }

            @Override
            protected String doInBackground(Integer...params) {
                return null;
            }
        }
        new GetLineCount(viewHolder).execute();

        RequestParams params = null;
        HttpUtil.post(HttpUtil.URLQueryID + article.articleAuthor, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String body = new String(responseBody, "GBK");
                            Document document = Jsoup.parse(body, "GBK");
                            Element imgSrc = document.getElementsByTag("img").first();
                            String downloadURL = imgSrc.attr("src");
                            if (!downloadURL.equals("/images/default_face.gif")) {
                                new DownloadImageTask((ImageView) view.findViewById(R.id.article_details_icon))
                                        .execute("http://bbs.ustc.edu.cn/cgi/" + downloadURL);
                            } else {
                                viewHolder.icon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_dialog_email));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });


        return view;
    }
}
