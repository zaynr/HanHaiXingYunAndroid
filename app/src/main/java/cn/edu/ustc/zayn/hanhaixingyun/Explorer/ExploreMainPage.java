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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
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

public class ExploreMainPage extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //variables
    private String utmpnum, utmpuserid, utmpkey;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ListView listView;
    private String board;
    private Document document;
    private ArrayList<ListItem> listItems;
    private IndexListItemAdapter indexListItemAdapter;

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
        setContentView(R.layout.activity_explore_main_page);
        sharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext(), "accountInfo");
        InitCookie();
        InitEvent();
        board = "tennis";
        ParseIndexPage();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.explore_main_page, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_explore_main_page, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ExploreMainPage) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
