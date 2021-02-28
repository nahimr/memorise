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
    private final byte MIN_LIGHTEN_BLOCK;
    private final byte MAX_LIGHTEN_BLOCK;
    private final byte MAX_LIVES;
    private final float WEIGHT;
    private final byte SPEED = 2;
    private final boolean TIMER;
    private float POINTS;
    private byte lives;
    private byte lightenBlocks;
    private byte numbersOfBlocks;
    private ArrayList<Byte> buttonsState;
    private ArrayList<Byte> playerAnswer;
    private byte level;

    protected Engine(byte MODE_CODE, byte MIN_LIGHTEN_BLOCK,
                     byte MAX_LIGHTEN_BLOCK,
                     byte MAX_LIVES, float WEIGHT, boolean TIMER) {
        this.MAX_LIGHTEN_BLOCK = MAX_LIGHTEN_BLOCK;
        this.MIN_LIGHTEN_BLOCK = MIN_LIGHTEN_BLOCK;
        this.MAX_LIVES = MAX_LIVES;
        this.lightenBlocks = MIN_LIGHTEN_BLOCK;
        this.numbersOfBlocks = MIN_BLOCK;
        this.lives = MAX_LIVES;
        this.level = (byte)1;
        this.POINTS = 0.0f;
        this.buttonsState = new ArrayList<>();
        this.playerAnswer = new ArrayList<>();
        this.MODE_CODE = MODE_CODE;
        this.WEIGHT = WEIGHT;
        this.TIMER = TIMER;
    }

    private void ResetLives(){
        this.lives = this.MAX_LIVES;
    }

    private void ResetPoints(){
        this.POINTS = 0;
    }

    public void StartGame(IGameStarted iGameStarted){
        iGameStarted.onGameStarted();
    }

    public void StartLevel(){
        RandomizeBlocksState();
        Log.i("DEBUG::", "l:"+this.level +" lb:" + this.lightenBlocks
                +"nB:" +this.numbersOfBlocks + " "+ this.buttonsState.toString() +
                ":");
    }

    public void EndGame(ILevel iLevel, IGameEnded iGameEnded){
        if(isLevelWon()){
            // Won level
            iLevel.onLevelFinished(true);
            // Giving out points
            CalculatePoints();
            // Check if there is another level
            if((this.level + 1)<= this.MAX_LEVEL){
                NextLevel();
            } else {
                Reset();
                iGameEnded.onGameEnded(true);
            }
        } else {
            // Loose Level
            iLevel.onLevelFinished(false);
            // Check if we have lives !
            if(!isGameOver()){
                StartLevel();
            } else {
                Reset();
                iGameEnded.onGameEnded(false);
            }
        }
    }

    public boolean isGameOver(){
        return this.lives<=0;
    }

    private void CalculatePoints(){
        this.POINTS += this.level * this.WEIGHT;
    }

    public void NextLevel(){
        /*
        TODO: Peut se rééclairer sur le même block
        Prévoire liste d'entier
         */
        this.level++;
        this.lightenBlocks = mapLightenBlocks(this.level);
        this.numbersOfBlocks = mapNumberOfBlocks(this.level);
        this.ResetLives();
        this.ClearLists();
        //StartLevel();
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