package com.dut2.memorise.game;

import android.os.CountDownTimer;
import android.view.View;
import com.dut2.memorise.game.events.*;
import com.dut2.memorise.game.utils.MathsUtility;
import java.util.*;
import java.util.concurrent.*;

public abstract class Engine {
    private final long BASE_TIMER = 2000;
    private long timeout;
    public static byte LEVEL_WON = 0;
    public static byte LEVEL_LOOSE = 1;
    public static byte END_GAME = 2;
    private final byte MAX_LEVEL = 7;
    private final byte MIN_BLOCK = 4;
    private final byte MAX_BLOCK = 10;
    private final byte minLightenBlock;
    protected byte maxLightenBlock;
    private final byte maxLives;
    private final float weight;
    private final boolean timer;
    private float points;
    private byte lives;
    private byte lightenBlocks;
    private byte numbersOfBlocks;
    private final BlockingQueue<Runnable> blockThreadList;
    public final ThreadPoolExecutor threadPoolExecutor;
    private final ArrayList<Byte> blockPattern;
    private final ArrayList<Byte> playerAnswer;
    private byte level;
    private CountDownTimer countDownTimer;
    protected IEngine iEngine;
    private IChange iChange;
    private ITimer iTimer;
    private boolean canPause;
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
        this.blockPattern = new ArrayList<>();
        this.playerAnswer = new ArrayList<>();
        this.weight = weight;
        this.timer = timer;
        blockThreadList = new LinkedBlockingQueue<>();
        this.canPause = false;
        this.threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    protected Engine(byte maxLives, float weight, boolean timer){
        this((byte)0, (byte)0, maxLives, weight, timer);
    }

    public void addBlockThread(Thread thread){
        this.blockThreadList.add(thread);
    }

    private void LightBlocks(){
        this.threadPoolExecutor.purge();
        this.threadPoolExecutor.submit(new Thread(()->{
            try {
                Thread.sleep(this.iEngine.onBeforeLevelStart());
                this.iEngine.onStartLevel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        for (Runnable runnable: this.blockThreadList){
            this.threadPoolExecutor.submit(runnable);
        }
        this.threadPoolExecutor.submit(this.iEngine.onLightenBlocksFinished());
        this.threadPoolExecutor.submit(()->{
            canPause = true;
        });
        if(this.timer){
            countDownTimer = new CountDownTimer(this.timeout,100){
                @Override
                public void onTick(long millisUntilFinished) {
                    Engine.this.iTimer.onTimerChange(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    Engine.this.iTimer.onTimerTimeout();
                    Engine.this.lives = 0;
                }
            };
            this.threadPoolExecutor.submit((Runnable) countDownTimer::start);
        }

        this.blockThreadList.clear();
    }

    public void StartLevel(){
        if(timer){
            this.iTimer.onTimerInit(this.timeout);
        }
        this.iEngine.onInitLevel();
        LoadLevel();
        LightBlocks();
        this.iChange.onChangeLevelsListener(this.level);
        this.iChange.onChangeLivesListener(this.lives);
    }

    private void EndLevel(){
        if(!timer){
            if(!isPlayerHasPlayed()) return;
        } else {
            countDownTimer.cancel();
            this.iTimer.onTimerFinish();
        }
        if(isLevelWon()){
            this.iEngine.onEndLevel(true,this.level);
            // Won level
            // Giving out points
            CalculatePoints();
            this.iChange.onChangePointsListener(this.points);
            // Check if there is another level
            if(!timer){
                if((this.level + 1)<= this.MAX_LEVEL){
                    this.Reset(Engine.LEVEL_WON);
                    this.level++;
                    StartLevel();
                } else {
                    this.iEngine.onEndGame(true, this.points);
                    this.Reset(Engine.END_GAME);
                }
            }else{
                this.Reset(Engine.LEVEL_WON);
                this.level++;
                StartLevel();
            }

        } else {
            this.iEngine.onEndLevel(false, this.level);
            // Loose Level
            // Check if we have lives !
            this.lives--;
            if(!isGameOver()){
                if(timer) this.lightenBlocks = 1;
                this.Reset(Engine.LEVEL_LOOSE);
                StartLevel();
            } else {
                this.iEngine.onEndGame(false, this.points);
                this.Reset(Engine.END_GAME);
            }
        }
        this.iChange.onChangeLivesListener(this.lives);
    }

    private boolean isGameOver(){
        return this.lives<=0;
    }

    private void CalculatePoints(){
        this.points += this.level * this.weight;
    }

    private void LoadLevel(){
        if(!timer){
            this.lightenBlocks = mapLightenBlocks();
        } else {
            this.lightenBlocks++;
            this.timeout = this.lightenBlocks * this.BASE_TIMER;
        }
        this.numbersOfBlocks = MathsUtility.clamp(mapNumberOfBlocks(),MIN_BLOCK,MAX_BLOCK);
        ShufflePattern();
        this.iChange.onChangeLevelsListener(this.level);
        for (byte pos = 0; pos < this.numbersOfBlocks; pos++) {
            this.iEngine.onLoadBlock(pos);
        }
        for (byte pos: this.blockPattern) {
            this.iEngine.onLoadLightenBlock(pos);
        }
    }

    private byte mapLightenBlocks(){
        return MathsUtility.mapping(this.level, (byte)1, MAX_LEVEL, minLightenBlock, maxLightenBlock);
    }

    private byte mapNumberOfBlocks(){
        return MathsUtility.mapping(this.level, (byte)1, MAX_LEVEL, MIN_BLOCK,MAX_BLOCK);
    }

    private boolean isLevelWon(){
        return this.blockPattern.equals(this.playerAnswer);
    }

    private void ShufflePattern(){
        for (byte i = 0; i < this.lightenBlocks; i++) {
            Random random = new Random();
            this.blockPattern.add((byte)(Math.abs(random.nextInt())%this.numbersOfBlocks));
        }
        Collections.shuffle(this.blockPattern);
    }

    public void addPlayerBlockAnswerAtPos(byte state){
        this.playerAnswer.add(state);
    }

    private void ResetLives(){
        this.lives = this.maxLives;
    }

    private void ResetPoints(){
        this.points = 0;
    }

    public void ClearLists(){
        this.playerAnswer.clear();
        this.blockPattern.clear();
    }

    public void Reset(byte resetState){
        this.ClearLists();
        if (resetState == Engine.LEVEL_WON){
            this.ResetLives();
        } else if(resetState == Engine.LEVEL_LOOSE){
            this.level = 1;
            if(timer){
                this.lightenBlocks = 0;
            }
        }else if(resetState == Engine.END_GAME){
            this.level = 1;
            this.ResetLives();
            this.ResetPoints();
        }
        this.iEngine.onReset(resetState);
    }

    public boolean isPlayerHasPlayed(){
        return this.playerAnswer.size() >= this.blockPattern.size();
    }

    public View.OnClickListener getBlockOnClickListener(byte blockPos){
        return (v -> {
            this.addPlayerBlockAnswerAtPos(blockPos);
            if(this.isPlayerHasPlayed()) this.EndLevel();
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

    public boolean CanPause(){
        return canPause;
    }

}