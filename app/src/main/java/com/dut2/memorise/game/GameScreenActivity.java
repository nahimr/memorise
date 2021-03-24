package com.dut2.memorise.game;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import com.dut2.memorise.R;
import com.dut2.memorise.authentication.utils.UserRepository;
import com.dut2.memorise.game.events.IChange;
import com.dut2.memorise.game.events.IEngine;
import com.dut2.memorise.game.events.ITimer;
import com.dut2.memorise.game.modes.Easy;
import com.dut2.memorise.game.modes.Expert;
import com.dut2.memorise.game.modes.Hard;
import com.dut2.memorise.game.modes.Timer;
import com.dut2.memorise.game.view.Block;
import com.dut2.memorise.utils.ThemeLoader;
import soup.neumorphism.NeumorphCardView;
import java.util.*;

public class GameScreenActivity extends AppCompatActivity {
    private GridLayout blocksLayout;
    private NeumorphCardView levelCardView;
    private NeumorphCardView notificationPopup;
    private NeumorphCardView pointsCardView;
    private NeumorphCardView timerCardView;
    private ImageView live1;
    private ImageView live2;
    private ImageView live3;
    private TextView notifiedText;
    private TextView pointText;
    private TextView levelText;
    private TextView timerText;
    private Animation firstReversedAnim;
    private Animation firstAnim;
    private Animation secondReversedAnim;
    private Animation secondAnim;
    private Animation blockReverseAnim;
    private byte mode;
    private Engine engine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeLoader.LoadTheme(this);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade(Fade.MODE_IN));
        getWindow().setExitTransition(new Fade(Fade.MODE_OUT));
        setContentView(R.layout.activity_game_screen);
        mode = getIntent().getByteExtra("mode",(byte)0);
        levelCardView = findViewById(R.id.levelCardView);
        notificationPopup = findViewById(R.id.notificationPopup);
        pointsCardView = findViewById(R.id.pointsCardView);
        timerCardView = findViewById(R.id.timerCardView);
        live1 = findViewById(R.id.live1);
        live2 = findViewById(R.id.live2);
        live3 = findViewById(R.id.live3);
        notifiedText = findViewById(R.id.notifText);
        pointText = findViewById(R.id.points);
        levelText = findViewById(R.id.level);
        timerText = findViewById(R.id.timer);
        final MediaPlayer cheersSound = MediaPlayer.create(this, R.raw.cheers_endlevel);
        final MediaPlayer badCheersSound = MediaPlayer.create(this, R.raw.badcheers_endlevel);
        badCheersSound.setVolume(85.f, 85.f);
        cheersSound.setVolume(85.f, 85.f);
        firstReversedAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_cardview_reverse).getAnimation();
        firstAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_cardview).getAnimation();
        secondReversedAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_lives_anim_reverse).getAnimation();
        secondAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_lives_anim).getAnimation();
        blockReverseAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_block_anim_reverse).getAnimation();
        blocksLayout = findViewById(R.id.blocksLayout);

        setLevel((byte)1);
        setPoints(0.0f);
        loadLives((byte)3);
        firstReversedAnim.setDuration(500);
        secondReversedAnim.setDuration(500);
        firstAnim.setDuration(500);
        secondAnim.setDuration(500);
        loadAnimations();
        firstAnim.startNow();
        secondAnim.startNow();

        int dimen = (int)getResources().getDimension(R.dimen.blockSize);
        Random rnd = new Random();

        IEngine engineListener = new IEngine() {
            @Override
            public void onStartLevel() {
                GameScreenActivity.this.notifiedText.setText(R.string.cpuPlaying);
                GameScreenActivity.this.blocksLayout.removeAllViews();
            }

            @Override
            public void onLoadBlock(byte pos) {
                Block block = new Block(GameScreenActivity.this);
                int color =
                        Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                block.setARGBColor(color);
                block.setEnabled(false);
                block.setOnClickListener(engine.getBlockOnClickListener(pos));
                block.clearAnimation();
                Animation tempAnim =
                        AnimationUtils.loadLayoutAnimation(GameScreenActivity.this, R.anim.layout_block_anim)
                                .getAnimation();
                tempAnim.setStartOffset(pos * 100);
                block.setAnimation(tempAnim);
                blocksLayout.addView(block,dimen,dimen);
            }

            @Override
            public void onLoadLightenBlock(byte pos) {
                engine.addBlockThread(((Block)blocksLayout.getChildAt(pos)).PressedThread(1000));
            }

            @Override
            public long onBeforeLevelStart() {
                // Time to wait in ms before start the game
                return 2000L;
            }

            @Override
            public void onStartPattern() {
                GameScreenActivity.this.notifiedText.setText(R.string.cpuPlaying);
            }

            @Override
            public void onEndPattern(boolean patternWon) {

            }

            @Override
            public Runnable onLightenBlocksFinished() {
                return GameScreenActivity.this.OnExecutionBlocksFinished();
            }

            @Override
            public void onEndLevel(boolean levelWon, byte levelCount) {
               if(levelWon && levelCount % 2 == 0) cheersSound.start();
               if(!levelWon) badCheersSound.start();
               GameScreenActivity.this.showOffBlocks(null);
            }

            @Override
            public void onEndGame(boolean gameWon, float points) {
                GameScreenActivity.this.runOnUiThread(()->
                        UserRepository.getInstance().updateUserScore(points, task -> {
                }, e -> Log.e("Memorise","Error!",e)));
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameScreenActivity.this);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(R.string.playAgain);
                dialog.setNegativeButton(R.string.yes, (dialog1, which) ->
                        engine.StartLevel()).setPositiveButton(R.string.no,(dialog1, which)->{
                    MediaPlayer.create(GameScreenActivity.this, R.raw.player_start).start();
                    GameScreenActivity.this.finish();
                }).show();
            }

            @Override
            public void onReset(byte resetState) {

            }
        };

        IChange changeListener = new IChange() {
            @Override
            public void onChangeLivesListener(byte numberOfLives) {
                GameScreenActivity.this.runOnUiThread(()->
                        GameScreenActivity.this.loadLives(numberOfLives));
            }
            @Override
            public void onChangePointsListener(float numberOfPoints){
                GameScreenActivity.this.runOnUiThread(()->
                        GameScreenActivity.this.setPoints(numberOfPoints));
            }
            @Override
            public void onChangeLevelsListener(byte levelNumber){
                GameScreenActivity.this.runOnUiThread(()->
                        GameScreenActivity.this.setLevel(levelNumber));
            }
        };

        ITimer timerListener = new ITimer() {
            @Override
            public void onTimerInit(long time) {
                timerCardView.setVisibility(View.VISIBLE);
                timerText.setVisibility(View.VISIBLE);
                GameScreenActivity.this.setTimerText(time);
            }

            @Override
            public void onTimerChange(long time) {
                GameScreenActivity.this.setTimerText(time);
            }

            @Override
            public void onTimerTimeout() {
                timerText.setText(R.string.timeOut);
                // TODO: Load Alert Dialog
            }

            @Override
            public void onTimerFinish() {
                timerText.setText(R.string.timerFinish);
            }
        };

        if(mode == 0) {
            engine = new Easy();
        } else if (mode == 1){
            engine = new Hard();
        } else if(mode == 2){
            engine = new Expert();
        } else if(mode == 3){
            engine = new Timer();
            engine.setOnTimerChangeListener(timerListener);
        }
        engine.setOnEventListener(engineListener);
        engine.setOnChangeListener(changeListener);
        engine.StartLevel();
    }

    public void setLevel(byte level){
        this.levelText.setText(getString(R.string.levelText,level));
    }

    private void setPoints(float points){
        this.pointText.setText(getString(R.string.pointsText,points));
    }

    @Override
    public void onBackPressed() {
        final MediaPlayer buttonSound = MediaPlayer.create(this, R.raw.player_start);
        buttonSound.start();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.exitDialog);
        dialog.setPositiveButton(R.string.yes, (dialog1, which) ->{
            MediaPlayer.create(this, R.raw.player_start).start();
            GameScreenActivity.this.runOnUiThread(()->
                    UserRepository.getInstance().updateUserScore(engine.getPoints(), task -> {
                    }, e -> Log.e("Memorise","Error!",e)));
            engine.KillThreads();
            this.showOffBlocks(()-> startReverseAnimations(this::finish));
        }).setNegativeButton(R.string.no,(dialog1, which)->
                dialog1.dismiss()).show();
    }

    private Runnable OnExecutionBlocksFinished(){
        final MediaPlayer transSound = MediaPlayer.create(GameScreenActivity.this, R.raw.win_trans);
        return () -> {
            GameScreenActivity.this.notifiedText.setText(R.string.yourTurn);
            transSound.start();
            GameScreenActivity.this.runOnUiThread(() -> {
                for (int i = 0; i < this.blocksLayout.getChildCount(); i++) {
                    this.blocksLayout.getChildAt(i).setEnabled(true);
                }
            });
        };
    }

    private void loadLives(byte lives){
        if(lives == 3){
            live1.setEnabled(true);
            live2.setEnabled(true);
            live3.setEnabled(true);
        } else if(lives == 2){
            live1.setEnabled(false);
            live2.setEnabled(true);
            live3.setEnabled(true);
        }else if(lives == 1){
            live1.setEnabled(false);
            live2.setEnabled(false);
            live3.setEnabled(true);
        } else {
            live1.setEnabled(false);
            live2.setEnabled(false);
            live3.setEnabled(false);
        }
    }

    private void loadAnimations(){
        if(mode == 3) timerCardView.setAnimation(secondAnim);
        notificationPopup.setAnimation(firstAnim);
        levelCardView.setAnimation(firstAnim);
        pointsCardView.setAnimation(firstAnim);
        live1.setAnimation(secondAnim);
        live2.setAnimation(secondAnim);
        live3.setAnimation(secondAnim);
    }

    private void loadReverseAnimations(){
        notificationPopup.setAnimation(firstReversedAnim);
        levelCardView.setAnimation(firstReversedAnim);
        pointsCardView.setAnimation(firstReversedAnim);
        if(mode == 3) timerCardView.setAnimation(secondReversedAnim);
        live1.setAnimation(secondReversedAnim);
        live2.setAnimation(secondReversedAnim);
        live3.setAnimation(secondReversedAnim);
    }

    private void clearAnimations(){
        levelCardView.clearAnimation();
        pointsCardView.clearAnimation();
        timerCardView.clearAnimation();
        live1.clearAnimation();
        live2.clearAnimation();
        live3.clearAnimation();
    }

    private void startReverseAnimations(Runnable runnable){
        clearAnimations();
        loadReverseAnimations();
        firstReversedAnim.startNow();
        secondReversedAnim.startNow();
        firstReversedAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                runnable.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    private void showOffBlocks(Runnable runnable){
        for (int i = 0; i < this.blocksLayout.getChildCount(); i++) {
            View block = this.blocksLayout.getChildAt(i);
            block.clearAnimation();
            block.setAnimation(this.blockReverseAnim);
        }
        this.blockReverseAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(runnable != null) runnable.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.blockReverseAnim.startNow();
    }

    private void setTimerText(long time){
        String timeFormat = String.format(getString(R.string.timerValueText),time);
        timerText.setText(timeFormat);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}