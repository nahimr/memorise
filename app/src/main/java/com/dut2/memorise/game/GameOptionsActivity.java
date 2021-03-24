package com.dut2.memorise.game;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.R;
import com.dut2.memorise.utils.ThemeLoader;

public class GameOptionsActivity extends AppCompatActivity {
    private MediaPlayer gameOptionSound;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeLoader.LoadTheme(this);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_game_options);

        gameOptionSound = MediaPlayer.create(this, R.raw.game_options);
        gameOptionSound.setVolume(50.0f,50.0f);
        gameOptionSound.setLooping(true);
        gameOptionSound.start();

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
        final MediaPlayer buttonSound = MediaPlayer.create(this, R.raw.btn_push);
        buttonSound.start();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        intent.putExtra("mode",option);
        startActivity(intent,options.toBundle());
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer.create(this, R.raw.player_start).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameOptionSound.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameOptionSound.start();
    }

}