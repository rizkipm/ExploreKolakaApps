package imastudio.rizki.com.explorekolaka;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import imastudio.rizki.com.explorekolaka.Activity.MainMenu;
import imastudio.rizki.com.explorekolaka.Activity.MenuUtama;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

              if (sesi.isLogin()){
                  startActivity(new Intent(c, MainMenu.class));
                  finish();

                }else {
                    startActivity(new Intent(c, LoginActivity.class));
                    finish();
                }
            }
        }, 2000L);
    }
}
