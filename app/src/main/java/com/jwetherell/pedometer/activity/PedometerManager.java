package com.jwetherell.pedometer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;

import com.jwetherell.pedometer.service.IStepService;
import com.jwetherell.pedometer.service.IStepServiceCallback;
import com.jwetherell.pedometer.service.StepService;

import java.util.logging.Logger;

/**
 * Created by kevinfinn on 2/8/15.
 */
public class PedometerManager {
    private static OnStepListener monStepListener;
    Context mcontext = null;
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock;
    private static final Logger logger = Logger.getLogger("PedometerManager");
    public static IStepService mService = null;
    public static Intent stepServiceIntent = null;

//    private static int sensitivity = 100;

    public PedometerManager(Context context){
        mcontext = context;

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Demo");

        if (stepServiceIntent == null) {
            Bundle extras = new Bundle();
            extras.putInt("int", 1);
            stepServiceIntent = new Intent(mcontext, StepService.class);
            stepServiceIntent.putExtras(extras);
        }

    }

    public static void setOnStepListener(OnStepListener onStepListener){
        monStepListener = onStepListener;
    }

    private static void notifyOnStepListener(int steps){
        if(monStepListener != null){
            monStepListener.onStep(steps);
        }
    }

    public void bind(){
        if (!wakeLock.isHeld()) wakeLock.acquire();

        // Bind without starting the service
        try {
            mcontext.bindService(stepServiceIntent, mConnection, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unbind(){

        if (wakeLock.isHeld()) wakeLock.release();

        unbindStepService();
    }

    static boolean isChecked = false;
    public void start() {
        logger.info("start");

        boolean serviceIsRunning = false;
        try {
            if (mService != null)
                serviceIsRunning = mService.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!serviceIsRunning) {
            startStepService();
            bindStepService();
        }
    }

    public void stop() {
        logger.info("stop");

        unbindStepService();
        stopStepService();
    }

    private void startStepService() {
        try {
            mcontext.startService(stepServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopStepService() {
        try {
            mcontext.stopService(stepServiceIntent);
        } catch (Exception e) {
            // Ignore
        }
    }

    private void bindStepService() {
        try {
            mcontext.bindService(stepServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindStepService() {
        try {
            mcontext.unbindService(mConnection);
        } catch (Exception e) {
            // Ignore
        }
    }

    protected static void stepsChanged(int value){
    }

    private static Handler handler = new Handler() {

        public void handleMessage(Message msg) {
//            int current = msg.arg1;
            //text.setText("Steps = " + current);
            if(Debug.isDebuggerConnected())
                Debug.waitForDebugger();
            notifyOnStepListener(msg.arg1);
            stepsChanged(msg.arg1);
        }
    };

    private static final IStepServiceCallback.Stub mCallback = new IStepServiceCallback.Stub() {

        @Override
        public IBinder asBinder() {
            return mCallback;
        }

        @Override
        public void stepsChanged(int value) throws RemoteException {
            logger.info("Steps=" + value);
            Message msg = handler.obtainMessage();
            msg.arg1 = value;
            handler.sendMessage(msg);
        }
    };

    private static final ServiceConnection mConnection = new ServiceConnection() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            logger.info("onServiceConnected()");
            mService = IStepService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
//                mService.setSensitivity(sensitivity);
//                startStopButton.setChecked(mService.isRunning());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //TODO
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName className) {
            logger.info("onServiceDisconnected()");
//            try {
//                startStopButton.setChecked(mService.isRunning());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            //TODO
            mService = null;
        }
    };

    private DialogInterface.OnClickListener yesStopClick = new DialogInterface.OnClickListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            stop();
        }
    };

    private DialogInterface.OnClickListener noStopClick = new DialogInterface.OnClickListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mService != null) try {
                isChecked = mService.isRunning();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public boolean isPedometerRunning() throws RemoteException{
        if(mService==null || !mService.isRunning())
            return false;
        else
            return true;
    }

}
