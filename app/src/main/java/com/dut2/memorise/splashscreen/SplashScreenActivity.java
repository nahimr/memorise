package com.dut2.memorise.splashscreen;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.MenuActivity;
import com.dut2.memorise.R;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            startActivity(new Intent(SplashScreenActivity.this, MenuActivity.class));
            finish();
        }, TIMEOUT_SPLASH);
    }
}
