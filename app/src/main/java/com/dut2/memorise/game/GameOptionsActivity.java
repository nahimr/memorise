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
        easyButton.setOnClickListener(v->{
            intent.putExtra("mode","easy");
            startActivity(intent);
            finish();
        });

        /*hardButton.setOnClickListener(v->runGame(Engine.HARD_CODE));
        expertButton.setOnClickListener(v->runGame(Engine.EXPERT_CODE));
        timerButton.setOnClickListener(v->runGame(Engine.TIMER_CODE));*/
    }
}
