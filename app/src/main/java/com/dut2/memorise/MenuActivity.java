package com.dut2.memorise;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dut2.memorise.authentication.LoginActivity;
import com.dut2.memorise.game.GameOptionsActivity;

public class MenuActivity extends AppCompatActivity {
    private ImageButton connectButton;
    private Button playButton;
    private Button leaderboardButton;
    private MediaPlayer menuSound;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade(Fade.MODE_IN));
        getWindow().setExitTransition(new Fade(Fade.MODE_OUT));
        setContentView(R.layout.activity_menu);
        connectButton = findViewById(R.id.connectButton);
        playButton = findViewById(R.id.playButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);
        menuSound = MediaPlayer.create(this, R.raw.game_menu);
        menuSound.setVolume(50.0f,50.0f);
        menuSound.setLooping(true);
        menuSound.start();

        final MediaPlayer buttonSound = MediaPlayer.create(this, R.raw.btn_push);
        connectButton.setOnClickListener(v ->{
            buttonSound.start();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(MenuActivity.this, LoginActivity.class),
                    options.toBundle());
        });

        playButton.setOnClickListener(v ->{
            buttonSound.start();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(new Intent(MenuActivity.this, GameOptionsActivity.class),
                    options.toBundle());
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        menuSound.pause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        menuSound.seekTo(0);
        menuSound.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaPlayer.create(this, R.raw.player_start).start();
    }
}