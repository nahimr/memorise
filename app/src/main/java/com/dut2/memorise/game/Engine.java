package com.dut2.memorise.game;

import android.util.Log;
import com.dut2.memorise.game.events.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public abstract class Engine {
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

    protected Engine(byte minLightenBlock,
                     byte maxLives, float weight, boolean timer){
        this(minLightenBlock, (byte)0, maxLives, weight, timer);
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
            countDownTimer = new CountDownTimer(5000,500){
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
            this.iTimer.onTimerInit();
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
            this.iEngine.onEndLevel(true);
            // Won level
            // Giving out points
            CalculatePoints();
            this.iChange.onChangePointsListener(this.points);
            // Check if there is another level
            if((this.level + 1)<= this.MAX_LEVEL){
                this.Reset(Engine.LEVEL_WON);
                this.level++;
                StartLevel();
            } else {
                this.Reset(Engine.END_GAME);
                this.iEngine.onEndGame(true, this.points);
            }
        } else {
            this.iEngine.onEndLevel(false);
            // Loose Level
            iLevel.onLevelFinished(false);
            // Check if we have lives !
            this.lives--;
            if(!isGameOver()){
                this.Reset(Engine.LEVEL_LOOSE);
                StartLevel();
            } else {
                this.Reset(Engine.END_GAME);
                this.iEngine.onEndGame(false, this.points);
            }
        }
        this.iChange.onChangeLivesListener(this.lives);
    }

    private boolean isGameOver(){
        return this.lives<=0;
    }

    private void CalculatePoints(){
        this.POINTS += this.level * this.WEIGHT;
    }

    private void LoadLevel(){
        if(!timer){
            this.lightenBlocks = mapLightenBlocks();
        } else {
            this.lightenBlocks++;
        }
        this.numbersOfBlocks = mapNumberOfBlocks();
        ShufflePattern();
        this.iChange.onChangeLevelsListener(this.level);
        for (byte pos = 0; pos < this.numbersOfBlocks; pos++) {
            this.iEngine.onLoadBlock(pos);
        }
        for (byte pos: this.blockPattern) {
            this.iEngine.onLoadLightenBlock(pos);
        }
    }

    private byte mapLightenBlocks(byte level){
        return mapping(level, (byte)1, MAX_LEVEL, MIN_LIGHTEN_BLOCK,MAX_LIGHTEN_BLOCK);
    }
    private byte mapNumberOfBlocks(byte level){
        return mapping(level, (byte)1, MAX_LEVEL, MIN_BLOCK,MAX_BLOCK);
    }

    private byte mapping(byte t, byte a, byte b, byte c, byte d){
        byte partA = (byte) (d-c);
        byte partB = (byte) (b-a);
        byte divide = (byte) (partA / partB);
        return (byte) (c + (divide* (t-a)));
    }

    public boolean isLevelWon(){
        Iterator<Byte> s1It = this.buttonsState.iterator();
        for (Byte aByte : this.playerAnswer) {
            if (s1It.next().equals(aByte)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkIfBlockEqualsAt(int pos, byte posState){
        return this.buttonsState.get(pos).equals(posState);
    }

    private void RandomizeBlocksState(){
        for (byte i = 0; i < this.lightenBlocks; i++) {
            Random random = new Random();
            this.buttonsState.add((byte)(Math.abs(random.nextInt())%this.numbersOfBlocks));
        }
        Collections.shuffle(this.buttonsState);
    }

    private byte getBlockStateAtPos(int pos){
        return this.buttonsState.get(pos);
    }

    public void setPlayerBlockStateAtPos(int pos, byte state){
        if(pos > this.lightenBlocks &&
                state > this.numbersOfBlocks) return;
        this.playerAnswer.set(pos,state);
    }

    public boolean isPlayerHasPlayed(){
        return this.playerAnswer.size() == this.buttonsState.size();
    }

    public void ClearLists(){
        this.playerAnswer.clear();
        this.buttonsState.clear();
    }

    public void Reset(){
        this.ClearLists();
        this.ResetLives();
        this.ResetPoints();
    }

    public byte getMODE_CODE() {
        return MODE_CODE;
    }

    public byte getMAX_LEVEL() {
        return MAX_LEVEL;
    }

    public byte getMIN_BLOCK() {
        return MIN_BLOCK;
    }

    public byte getMAX_BLOCK() {
        return MAX_BLOCK;
    }

    public float getWEIGHT() {
        return WEIGHT;
    }

    public byte getSPEED() {
        return SPEED;
    }

    public byte getMAX_LIVES() {
        return MAX_LIVES;
    }

    public float getPOINTS() {
        return POINTS;
    }

    public boolean isTIMER(){
        return this.TIMER;
    }

    public byte getLightenBlocks() {
        return lightenBlocks;
    }

    public byte getNumbersOfBlocks() {
        return numbersOfBlocks;
    }

    public byte getLevel() {
        return level;
    }

    public ArrayList<Byte> getButtonsState() {
        return buttonsState;
    }
}