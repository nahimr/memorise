package com.dut2.memorise.game;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dut2.memorise.R;
import com.dut2.memorise.game.adapter.BlockAdapter;
import com.dut2.memorise.game.modes.Easy;
import soup.neumorphism.NeumorphCardView;
import java.util.*;

public class GameScreenActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Map<Integer, Boolean> blocs;
    private NeumorphCardView levelCardView;
    private NeumorphCardView pointsCardView;
    private ImageView live1;
    private ImageView live2;
    private ImageView live3;
    private TextView pointText;
    private TextView levelText;
    private BlockAdapter blockAdapter;
    Animation firstReversedAnim;
    Animation firstAnim;
    Animation secondReversedAnim;
    Animation secondAnim;
    private Easy easy;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        byte mode = getIntent().getByteExtra("mode",(byte)0);
        levelCardView = findViewById(R.id.levelCardView);
        pointsCardView = findViewById(R.id.pointsCardView);
        recyclerView = findViewById(R.id.blocks);
        live1 = findViewById(R.id.live1);
        live2 = findViewById(R.id.live2);
        live3 = findViewById(R.id.live3);
        pointText = findViewById(R.id.points);
        levelText = findViewById(R.id.level);
        firstReversedAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_cardview_reverse).getAnimation();
        firstAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_cardview).getAnimation();
        secondReversedAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_lives_anim_reverse).getAnimation();
        secondAnim =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_lives_anim).getAnimation();
        setLevel((byte)1);
        setPoints(0.0f);
        blocs = new HashMap<>();
        blockAdapter = new BlockAdapter(this, blocs);
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.setAdapter(blockAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.suppressLayout(true);
        recyclerView.setNestedScrollingEnabled(false);
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
            public void onInitLevel() {
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
                blocksLayout.addView(block,dimen,dimen);
            }

            @Override
            public void onLoadLightenBlock(byte pos) {
                engine.addBlockThread(((Block)blocksLayout.getChildAt(pos)).PressedThread(1000));
            }

            @Override
            public long onBeforeLevelStart() {
                GameScreenActivity.this.runOnUiThread(()->
                        Toast.makeText(GameScreenActivity.this,"Level is starting",
                                Toast.LENGTH_SHORT).show());
                return 2000L; // Time to wait before start
            }

            @Override
            public void onStartLevel() {
                GameScreenActivity.this.runOnUiThread(()->
                        Toast.makeText(GameScreenActivity.this,"Level has started",
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public Runnable onLightenBlocksFinished() {
                return GameScreenActivity.this.OnExecutionBlocksFinished();
            }

            @Override
            public void onEndLevel(boolean levelWon) {
            }

            @Override
            public void onEndGame(boolean gameWon, float points) {
                String endMessage = "%s:%.2f Points";
                Toast.makeText(GameScreenActivity.this,
                        gameWon?String.format(endMessage,"Win",points):
                                String.format(endMessage,"Loose",points),
                        Toast.LENGTH_LONG).show();
                GameScreenActivity.this.finish();
            }

            @Override
            public void onReset(byte resetState) {
                if(resetState == Engine.LEVEL_WON){

                } else if(resetState == Engine.LEVEL_LOOSE){

                } else if(resetState == Engine.END_GAME){

                }
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
            public void onTimerInit() {
                timerText.setVisibility(View.VISIBLE);
                timerText.setText("Timer");
            }

            @Override
            public void onTimerChange(long time) {
                timerText.setText(String.format(getString(R.string.timerValueText),time));
            }

            @Override
            public void onTimerTimeout() {
                timerText.setText("Timeout!");
            }

            @Override
            public void onTimerFinish() {
                timerText.setVisibility(View.INVISIBLE);
            }
        };

        if(mode == 0) {
            engine = new Easy();
            engine.setOnEventListener(engineListener);
            engine.setOnChangeListener(changeListener);
        } else if (mode == 1){
            engine = new Hard();
            engine.setOnEventListener(engineListener);
            engine.setOnChangeListener(changeListener);
        } else if(mode == 2){
            engine = new Expert();
            engine.setOnEventListener(engineListener);
            engine.setOnChangeListener(changeListener);
        } else if(mode == 3){
            engine = new Timer();
            engine.setOnEventListener(engineListener);
            engine.setOnChangeListener(changeListener);
            engine.setOnTimerChangeListener(timerListener);
        }
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
        // TODO: Pause the game here !
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.exitDialog);
        dialog.setPositiveButton("YES", (dialog1, which) ->
                startReverseAnimations(this::finish)).setNegativeButton("NO",(dialog1, which)->
                dialog1.dismiss()).show();
    }

    private Runnable OnExecutionBlocksFinished(){
        return () -> GameScreenActivity.this.runOnUiThread(() -> {
            for (int i = 0; i < this.blocksLayout.getChildCount(); i++) {
                this.blocksLayout.getChildAt(i).setEnabled(true);
            }
        });
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
        levelCardView.setAnimation(firstAnim);
        pointsCardView.setAnimation(firstAnim);
        live1.setAnimation(secondAnim);
        live2.setAnimation(secondAnim);
        live3.setAnimation(secondAnim);
    }

    private void loadReverseAnimations(){
        levelCardView.setAnimation(firstReversedAnim);
        pointsCardView.setAnimation(firstReversedAnim);
        live1.setAnimation(secondReversedAnim);
        live2.setAnimation(secondReversedAnim);
        live3.setAnimation(secondReversedAnim);
    }

    private void clearAnimations(){
        levelCardView.clearAnimation();
        pointsCardView.clearAnimation();
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
}