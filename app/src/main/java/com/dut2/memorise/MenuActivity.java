package com.dut2.memorise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.authentication.LoginActivity;
import com.dut2.memorise.game.GameOptionsActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final ImageButton connectButton = findViewById(R.id.connectButton);
        final Button playButton = findViewById(R.id.playButton);
        final Button leaderboardButton = findViewById(R.id.leaderboardButton);
        connectButton.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, LoginActivity.class)));
        playButton.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, GameOptionsActivity.class)));
    }
}