package com.dut2.memorise.authentication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.MenuActivity;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.UserRepository;

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
        loginButton.setOnClickListener(v->
                UserRepository.getInstance().authUser(this, email.getText().toString(),
                        password.getText().toString(),
                task -> {
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class),
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    }else {
                        Toast.makeText(this, "Wrong password or email !", Toast.LENGTH_SHORT).show();
                    }
                }, e -> Toast.makeText(this, "Erreur de connexion !", Toast.LENGTH_SHORT).show()));

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
