package com.dut2.memorise.game;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import com.dut2.memorise.game.events.*;
import com.dut2.memorise.game.utils.MathsUtility;
import java.util.*;
import java.util.concurrent.*;

public abstract class Engine {
    private final long BASE_TIMER = 2000;
    private long timeout;
    public static final byte START_LEVEL = 0;
    public static final byte START_PATTERN = 1;
    public static final byte END_PATTERN = 2;
    public static final byte RESET_GAME = 3;
    private final byte MAX_LEVEL = 7;
    private final byte MIN_BLOCK = 4;
    private final byte MAX_BLOCK = 10;
    private byte lightenBlockCounter = 1;
    private final byte minLightenBlock;
    protected final byte maxLightenBlock;
    private final byte maxLives;
    private final float weight;
    private final boolean timer;
    private float points;
    private byte lives;
    private byte lightenBlocks;
    private byte numbersOfBlocks;
    private final BlockingQueue<Runnable> blockThreadList;
    public final ThreadPoolExecutor threadPoolExecutor;
    private final ArrayList<Byte> lightPattern;
    private final ArrayList<Byte> playerAnswer;
    private byte level;
    private CountDownTimer countDownTimer;
    protected IEngine iEngine;
    private IChange iChange;
    private ITimer iTimer;
    protected Engine(byte minLightenBlock,
                     byte maxLightenBlock,
                     byte maxLives, float weight, boolean timer) {
        this.maxLightenBlock = maxLightenBlock;
        this.minLightenBlock = minLightenBlock;
        this.maxLives = maxLives;
        this.lightenBlocks = minLightenBlock;
        this.numbersOfBlocks = MIN_BLOCK;
        this.lives = maxLives;
        this.level = (byte)1;
        this.points = 0.0f;
        this.lightPattern = new ArrayList<>();
        this.playerAnswer = new ArrayList<>();
        this.weight = weight;
        this.timer = timer;
        blockThreadList = new LinkedBlockingQueue<>();
        this.threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    protected Engine(byte maxLives, byte maxLightenBlock, float weight, boolean timer){
        this((byte)1, maxLightenBlock, maxLives, weight, timer);
    }

    public void addBlockThread(Thread thread){
        this.blockThreadList.add(thread);
    }

    private void LightBlocks(){
        this.threadPoolExecutor.purge();
        this.threadPoolExecutor.submit(new Thread(()->{
            try {
                Thread.sleep(this.iEngine.onBeforeLevelStart());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        for (Runnable runnable: this.blockThreadList){
            this.threadPoolExecutor.submit(runnable);
        }
        this.threadPoolExecutor.submit(this.iEngine.onLightenBlocksFinished());
        if(this.timer){
            // Refresh ratio settled to 100 ms
            countDownTimer = new CountDownTimer(this.timeout,100){
                @Override
                public void onTick(long millisUntilFinished) {
                    Engine.this.iTimer.onTimerChange(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    Engine.this.iTimer.onTimerTimeout();
                    Engine.this.iEngine.onEndGame(true,Engine.this.points);
                    // TODO : Lives equal to 0 ? check this shit
                    Engine.this.lives = 0;
                }
            };
            this.threadPoolExecutor.submit((Runnable) countDownTimer::start);
        }
        this.blockThreadList.clear();
    }

    public void StartLevel(){
        this.Reset(Engine.START_LEVEL);
        Log.d("Memorise", "Start Level");
        if(timer){
            this.iTimer.onTimerInit(this.timeout);
        }
        this.iEngine.onStartLevel();
        LoadLevel();
        this.iChange.onChangeLevelsListener(this.level);
        this.iChange.onChangeLivesListener(this.lives);
        StartPattern();
    }

    private void StartPattern(){
        this.Reset(Engine.START_PATTERN);
        // Add to block pattern new lighten block
        Log.d("Memorise", "Start Pattern");
        LoadPattern();
        LightBlocks();
        this.iEngine.onStartPattern();
    }

    private void EndPattern(){
        Log.d("Memorise", "End Pattern");
        // Check if this is the correct pattern
        if(!timer){
            if(!isPlayerHasPlayed()) return;
        } else {
            countDownTimer.cancel();
            this.iTimer.onTimerFinish();
        }

        if(isPatternWon()){
            this.lives = this.maxLives;
            this.iEngine.onEndPattern(true);
            if(this.lightenBlocks == maxLightenBlock){ // Is last pattern ?
                EndLevel(true);
            }else{
                StartPattern();
            }
        } else {
            // Reset to first level
            EndLevel(false);
            this.iEngine.onEndPattern(false);
        }
        this.Reset(Engine.END_PATTERN);
        this.iChange.onChangeLivesListener(this.lives);
    }

    private void NextPattern(){
        // Add pattern !
        if(!timer){
            if(this.lightenBlocks == this.maxLightenBlock){
                this.lightenBlocks = this.minLightenBlock;
            } else {
                this.lightenBlocks = mapLightenBlocks(this.lightenBlockCounter);
                lightenBlockCounter++;
            }
        } else {
            this.lightenBlocks++;
            this.timeout = this.lightenBlocks * this.BASE_TIMER;
        }
    }

    private void EndLevel(boolean isLevelWon){
        Log.d("Memorise", "End level");

        if(!isGameOver()){ // Is Player Game Over ?
            if(isLevelWon){ // Has Player Won level ?
                // Giving out points
                this.iEngine.onEndLevel(true,this.level);
                CalculatePoints();
                if(!timer){ // Does mode haven't got timer ?
                    if((this.level + 1) <= this.MAX_LEVEL){ // Check if max level reached out
                        this.level++;
                        StartLevel();
                    } else {
                        this.iEngine.onEndGame(true, this.points);
                    }
                }else{
                    this.lightenBlocks = minLightenBlock;
                    this.level++;
                    StartLevel();
                }
            } else {
                this.iEngine.onEndLevel(false,this.level);
                this.level = 1;
                this.lives--;
                if(!isGameOver()){
                    StartLevel();
                } else {
                    this.iEngine.onEndGame(false, this.points);
                }

            }
        } else {
            this.iEngine.onEndGame(false, this.points);
        }

        // Calling delegates !
        this.iChange.onChangePointsListener(this.points);
        this.iChange.onChangeLivesListener(this.lives);
    }

    private boolean isGameOver(){
        return this.lives<=0;
    }

    private void CalculatePoints(){
        this.points += this.level * this.weight;
    }

    private void LoadLevel(){
        this.numbersOfBlocks = MathsUtility.clamp(mapNumberOfBlocks(),MIN_BLOCK,MAX_BLOCK);
        this.iChange.onChangeLevelsListener(this.level);
        for (byte pos = 0; pos < this.numbersOfBlocks; pos++) {
            this.iEngine.onLoadBlock(pos);
        }
    }

    private void LoadPattern(){
        NextPattern();
        ShufflePattern();
    }

    private byte mapLightenBlocks(byte value){
        return MathsUtility.mapping(value, (byte)1, MAX_LEVEL, minLightenBlock, maxLightenBlock);
    }

    private byte mapNumberOfBlocks(){
        return MathsUtility.mapping(this.level, (byte)1, MAX_LEVEL, MIN_BLOCK,MAX_BLOCK);
    }

    private boolean isPatternWon(){
        return this.lightPattern.equals(this.playerAnswer);
    }

    private void ShufflePattern(){
        List<Byte> lightPatternToAdd = new ArrayList<>();
        for(byte i = (byte)this.lightPattern.size(); i < this.lightenBlocks; i++){
            Random random = new Random();
            lightPatternToAdd.add((byte)(Math.abs(random.nextInt())%this.numbersOfBlocks));
        }
        Collections.shuffle(lightPatternToAdd);
        this.lightPattern.addAll(lightPatternToAdd);
        Log.d("Memorise","Light Pattern:"+this.lightPattern.toString());
        this.lightPattern.forEach(aByte -> this.iEngine.onLoadLightenBlock(aByte));
    }

    public void addPlayerBlockAnswerAtPos(byte state){
        this.playerAnswer.add(state);
    }

    public void Reset(byte resetState){
        switch (resetState){
            case Engine.START_LEVEL:
                this.lightenBlocks = minLightenBlock;
                this.lightenBlockCounter = 1;
                this.lightPattern.clear();
                this.blockThreadList.clear();
                this.playerAnswer.clear();
                break;
            case Engine.START_PATTERN:
                this.playerAnswer.clear();
                this.blockThreadList.clear();
                break;
            case Engine.RESET_GAME:
                this.lightenBlocks = minLightenBlock;
                this.lightenBlockCounter = 1;
                this.numbersOfBlocks = MIN_BLOCK;
                this.lives = maxLives;
                this.level = 1;
                this.points = 0.0f;
                this.lightPattern.clear();
                this.blockThreadList.clear();
                this.playerAnswer.clear();
                this.iChange.onChangeLevelsListener(this.level);
                this.iChange.onChangeLivesListener(this.lives);
                this.iChange.onChangePointsListener(this.points);
                break;
        }

        this.iEngine.onReset(resetState);
    }

    public boolean isPlayerHasPlayed(){
        return this.playerAnswer.size() >= this.lightPattern.size();
    }

    public View.OnClickListener getBlockOnClickListener(byte blockPos){
        return (v -> {
            this.addPlayerBlockAnswerAtPos(blockPos);
            if(this.isPlayerHasPlayed()) this.EndPattern();
        });
    }

    public void setOnEventListener(IEngine iEngine){
        this.iEngine = iEngine;
    }

    public void setOnChangeListener(IChange iChange) {
        this.iChange = iChange;
    }

    public void setOnTimerChangeListener(ITimer iTimer) {
        this.iTimer = iTimer;
    }

    public void KillThreads(){
        this.threadPoolExecutor.shutdownNow();
    }

    public float getPoints() {
        return points;
    }
}