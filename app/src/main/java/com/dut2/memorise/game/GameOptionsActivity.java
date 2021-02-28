package com.dut2.memorise.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;

public class GameOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);
        final Button easyButton = findViewById(R.id.easyButton);
        final Button hardButton = findViewById(R.id.hardButton);
        final Button expertButton = findViewById(R.id.expertButton);
        final Button timerButton = findViewById(R.id.timerButton);
        Intent intent = new Intent(GameOptionsActivity.this,
                GameScreenActivity.class);
        easyButton.setOnClickListener(v -> LoadGameMode(intent,(byte)0));

        hardButton.setOnClickListener(v -> LoadGameMode(intent,(byte)1));

        expertButton.setOnClickListener(v->LoadGameMode(intent,(byte)2));

        timerButton.setOnClickListener(v->LoadGameMode(intent,(byte)3));
    }

    private void LoadGameMode(Intent intent,byte option){
        intent.putExtra("mode",option);
        startActivity(intent);
        finish();
    }

}
