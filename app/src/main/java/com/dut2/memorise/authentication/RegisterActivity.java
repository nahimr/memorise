package com.dut2.memorise.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);
        final EditText email = findViewById(R.id.register_email);
        final EditText username = findViewById(R.id.register_username);
        final EditText password = findViewById(R.id.register_password);
        final Button loginButton = findViewById(R.id.loginButton);
        final Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
