package com.dut2.memorise.authentication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.User;
import com.dut2.memorise.authentication.utils.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_auth_register);
        final EditText email = findViewById(R.id.register_email);
        final EditText username = findViewById(R.id.register_username);
        final EditText password = findViewById(R.id.register_password);
        final Button loginButton = findViewById(R.id.loginButton);
        final Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            user = new User(email.getText().toString(),
                    username.getText().toString(), password.getText().toString());
            UserRepository.getInstance().addUser(this, user, (error, databaseReference) ->{
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    finish();
                    },
                    e -> Log.e("Memorise",getString(R.string.errorSignup), e));
        });

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class),
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
