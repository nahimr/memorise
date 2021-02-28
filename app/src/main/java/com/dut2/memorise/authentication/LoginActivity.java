package com.dut2.memorise.authentication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_auth_login);
        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        final Button loginButton = findViewById(R.id.login_loginButton);
        final Button registerButton = findViewById(R.id.login_registerButton);

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final MediaPlayer buttonSound = MediaPlayer.create(this, R.raw.player_start);
        buttonSound.start();
    }
}
