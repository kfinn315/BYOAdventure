package com.bingcrowsby.byoadventure.Controller;

import android.content.Context;
import android.util.Log;

import com.bingcrowsby.byoadventure.Model.MySharedPrefs;

/**
 * Created by kevinfinn on 2/20/15.
 */
public class UserStateManager { //singleton
    private static final String TAG = "user state manager";
    public static final int DEFAULT_HEALTH = 5;
    public static final int DEFAULT_LIVES = 5;
    private static int mhealth = 0, mlives = 0;
    private static final float DAILY_HEALTH_DECREASE = 1.5f;

    static private UserStateManager userStateManager = null;

    HealthChangeListener mhealthChangeListener;
    LivesChangeListener mlivesChangeListener;

    public interface HealthChangeListener{
        public void onHealthChange(int newHealth);
        public void onHealthIsZero();
    }
    public interface LivesChangeListener{
        public void onLivesChange(int newLives);
        public void onLifeIsZero();
    }

    private UserStateManager(UserConfig userConfig){
        mhealth = userConfig.health;
        mlives = userConfig.lives;
    }

    static public UserStateManager getInstance(Context context){
        if(userStateManager==null){
            UserConfig userConfig = MySharedPrefs.getUserConfig(context);
            userStateManager = new UserStateManager(userConfig);
        }
        return userStateManager;
    }

    public void setUserState(int health, int lives){ //don't call listeners
        mhealth = health;
        mlives = lives;
    }

    public int getHealth() {
        return mhealth;
    }

    public void setHealth(int health){
        Log.i(TAG,"setting health to "+health);
        assert(health >= 0);

//            notifyHealthChanged(health);
//            if(mhealth <= 0 ) {
//                notifyHealthIsZero();
//                decreaseLife();
//                if(mlives > 0)
//                    resetHealth();
//                else
//                    notifyLifeIsZero();
//            }
    }
    public void decreaseHealth(){
        decreaseHealth(1);
    }
    public void decreaseHealth(int damage){
        //PostCondition: mhealth >= 0, mlives >= 0,
        //   POSTVALUE(mlives*DEFAULT_LIVES+mhealth) = damage + PREVALUE(mlives*DEFAULT_LIVES+mhealth)
        assert(damage >= 0);

        if(damage >= mhealth){
            damage = damage - mhealth;
            mhealth = 0;
            notifyHealthChanged(mhealth);
            notifyHealthIsZero();
            decreaseLife();
            if(mlives > 0 && damage > 0)
                decreaseHealth(damage);
        } else{ //damage < mhealth
            mhealth = mhealth - damage;
            notifyHealthChanged(mhealth);
        }
    }

    public void resetHealth(){
        setHealth(DEFAULT_HEALTH);
    }

    public int getLives(){
        return  mlives;
    }
    public void setLives(int lives){
        assert(lives >= 0);
        Log.i(TAG, "setting lives to "+lives);
        mlives = lives;
    }
    public void decreaseLife(){
        decreaseLife(1);
    }
    public void decreaseLife(int damage){
        if(mlives >= damage) {
            mlives = 0;
            notifyLifeIsZero();
        }
        else
            mlives = mlives - damage;
    }
    public void resetLife(){
        setLives(DEFAULT_LIVES);
    }

    public void setHealthChangeListener(HealthChangeListener healthChangeListener){
        mhealthChangeListener = healthChangeListener;
    }
    public void setLivesChangeListener(LivesChangeListener livesChangeListener){
        mlivesChangeListener = livesChangeListener;
    }
    private void notifyHealthChanged(int newHealth){
        if(mhealthChangeListener!=null){
            mhealthChangeListener.onHealthChange(newHealth);
        }
    }
    private void notifyLivesChanged(int newLives){
        if(mlivesChangeListener!=null){
            mlivesChangeListener.onLivesChange(newLives);
        }
    }
    private void notifyHealthIsZero(){
        if(mhealthChangeListener!=null){
            mhealthChangeListener.onHealthIsZero();
        }
    }
    private void notifyLifeIsZero(){
        if(mlivesChangeListener!=null){
            mlivesChangeListener.onLifeIsZero();
        }
    }

    public void advanceDays(long days){
        Log.i(TAG, "advancing user state " + days + " days");
        int healthDecrease = Math.round(((float)days)*DAILY_HEALTH_DECREASE);
        decreaseHealth(healthDecrease);
    }

}
