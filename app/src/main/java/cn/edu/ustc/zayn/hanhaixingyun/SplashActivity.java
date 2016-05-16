package cn.edu.ustc.zayn.hanhaixingyun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import cn.edu.ustc.zayn.hanhaixingyun.Explorer.ExploreMainPage;
import cn.edu.ustc.zayn.hanhaixingyun.utils.SharedPreferenceUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((ImageView) findViewById(R.id.start_page)).postDelayed(new Runnable() {

            @Override
            public void run() {
                SharedPreferenceUtil util = new SharedPreferenceUtil(
                        getApplicationContext(), "accountInfo");
                String isLogin = util.getKeyData("isLogged in");
                //是否已登录
                if (isLogin.equals("TRUE")) {
                    Intent intent = new Intent(SplashActivity.this,
                            ExploreMainPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}
