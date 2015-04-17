package com.bingcrowsby.byoadventure.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Random;

/**
 * Created by kevinfinn on 2/21/15.
 */
public class AdventureEventManager {
    /* random events can happen during an adventure
    /* this class controls the timing and the effects on gameplay of these events */

    static final float oddsOfEvent = 0.25f;
    static final float oddsOfDamage = 0.1f;
    Random rand;

    public AdventureEventManager(){
        rand = new Random();
    }

    public EventObject getEvent(int day){ //after a turn (step or day?), all getEvent to receive an eventobject or null
       // if(rand.nextFloat() < oddsOfEvent)
            return new EventObject("event","descr", day, rand.nextInt(6)-3, rand.nextInt(6),1);
       // else
       //     return null;
    }

    public class EventObject{
        protected String mtitle = null, mdescription = null;
        protected int mdamage = 0;
        protected int mstepdist = 0;
        protected int mstepfactor = 1;
        protected int mday = 0;

        public EventObject(String title, String description, int day, int damage, int stepdist, int stepfactor){
            mtitle = title;
            mdescription = description;
            mdamage = damage;
            mstepdist = stepdist;
            mstepfactor = stepfactor;
            mday = day;
        }
        public int getDamage(){
            return mdamage;
        }

        public int getStepDistance(){
            return mstepdist;
        }

        public int getStepDistanceFactor(){
            return mstepfactor;
        }

        public String getDescription(){
            StringBuilder builder = new StringBuilder();
            builder.append(mtitle).append(" "+mdescription).append(" RESULTING DAMAGE: ").append(mdamage).append("\nResulting PACE: ").append(mstepdist).append(" and pacefactor: ").append(mstepfactor);
            return builder.toString();
        }
    }

    static public class EventAlert {
        static private AlertDialog alertDialog = null;

        public static AlertDialog getInstance(Context context, EventObject event) {
            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(context).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).create();
                //customize alert here
            }
            alertDialog.setTitle("Event");
            alertDialog.setMessage(event.getDescription());
            return alertDialog;
        }
    }

}
