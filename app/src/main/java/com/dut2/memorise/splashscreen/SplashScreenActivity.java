package com.dut2.memorise.splashscreen;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.MenuActivity;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.LoginActivity;
import com.dut2.memorise.authentication.utils.UserRepository;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade(Fade.MODE_IN));
        getWindow().setExitTransition(new Fade(Fade.MODE_OUT));
        setContentView(R.layout.activity_splashscreen);
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            this.setTheme(R.style.NeumorphicDark);
        } else {
            this.setTheme(R.style.NeumorphicLight);
        }

        short TIMEOUT_SPLASH = 3000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent whereToGo = new Intent(SplashScreenActivity.this, LoginActivity.class);
            if(UserRepository.getInstance().isCurrentUserExists()){
                whereToGo = new Intent(SplashScreenActivity.this, MenuActivity.class);
            }

            startActivity(whereToGo,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }, TIMEOUT_SPLASH);
    }
}
