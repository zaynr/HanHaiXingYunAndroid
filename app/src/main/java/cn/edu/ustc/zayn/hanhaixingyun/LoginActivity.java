package cn.edu.ustc.zayn.hanhaixingyun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.litepal.tablemanager.Connector;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.ustc.zayn.hanhaixingyun.Explorer.ExploreMainPage;
import cn.edu.ustc.zayn.hanhaixingyun.utils.CommonUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.HttpUtil;
import cn.edu.ustc.zayn.hanhaixingyun.utils.SharedPreferenceUtil;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    //variables
    private Intent intent;
    private EditText idField, passworldField;
    private Button loginButton, anonymousLoginButton;
    private HashMap<String, String> cookie;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    Login();
                    break;
                case R.id.anonymousLoginButton:
                    anonymousLogin();
                    break;
            }
        }
    };

    private void anonymousLogin() {
        final ProgressDialog progressDialog = CommonUtil.getProcessDialog(this, "Logging in...");
        progressDialog.show();
        Toast.makeText(getApplicationContext(), "匿名登录，发/回帖需要实名！", Toast.LENGTH_SHORT).show();
        sharedPreferenceUtil.setKeyData("isLogged in", "FALSE");

        //Start exploring the web page
        intent = new Intent(LoginActivity.this, ExploreMainPage.class);
        startActivity(intent);
        finish();
    }

    private Runnable loginThread = new Runnable() {
        @Override
        public void run() {
            try{
                Connection.Response response = Jsoup.connect(HttpUtil.URLLogin)
                        .data("id",HttpUtil.id,"pw",HttpUtil.password,"x",HttpUtil.x,"y",HttpUtil.y)
                        .method(Connection.Method.POST)
                        .execute();
                Document document = response.parse();
                HashMap<String, String> cookie = new HashMap<>();
                cookie.put("utmpnum",response.cookie("utmpnum"));
                cookie.put("utmpkey",response.cookie("utmpkey"));
                cookie.put("utmpuserid", response.cookie("utmpuserid"));
                Looper.prepare();
                if(document.toString().contains("密码错误")){
                    Toast.makeText(getApplicationContext(),"密码错误!",Toast.LENGTH_SHORT).show();
                }
                else if(document.toString().contains("错误的使用者帐号!")){
                    Toast.makeText(getApplicationContext(),"错误的使用者帐号!",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                    sharedPreferenceUtil.setKeyData("_ga","GA1.3.2003984802.1448463001");
                    sharedPreferenceUtil.setKeyData("utmpnum", cookie.get("umpnum"));
                    sharedPreferenceUtil.setKeyData("utmpkey", cookie.get("utmpkey"));
                    sharedPreferenceUtil.setKeyData("utmpuserid", cookie.get("utmpuserid"));
                    sharedPreferenceUtil.setKeyData("isLogged in", "TRUE");
                    intent = new Intent(LoginActivity.this, ExploreMainPage.class);
                    startActivity(intent);
                    finish();
                }
                Looper.loop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void Login() {
        HttpUtil.id = idField.getText().toString();
        HttpUtil.password = passworldField.getText().toString();
        if (HttpUtil.id.matches("") || HttpUtil.password.matches("")) {
            Toast.makeText(getApplicationContext(), "账号或密码为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(HttpUtil.id.contains("guest")){
            anonymousLogin();
            return;
        }
        final ProgressDialog progressDialog = CommonUtil.getProcessDialog(this, "Logging in...");
        progressDialog.show();
        try {
            new Thread(loginThread).start();
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            progressDialog.dismiss();
        }
    }

    private void InitViews() {
        idField = (EditText) findViewById(R.id.loginId);
        passworldField = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        anonymousLoginButton = (Button) findViewById(R.id.anonymousLoginButton);
    }

    private void InitEvents() {
        loginButton.setOnClickListener(onClickListener);
        anonymousLoginButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitViews();
        InitEvents();
        sharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext(), "accountInfo");

    }

}