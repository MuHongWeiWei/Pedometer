package com.fly.pedometer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.fly.pedometer.Interface.StepValuePassListener;
import com.fly.pedometer.Interface.UpdateUiCallBack;
import com.fly.pedometer.Utils.StepCount;
import com.fly.pedometer.Utils.StepDetector;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

public class StepService extends Service{
    private final IBinder mBinder = new StepBinder();
    private UpdateUiCallBack mCallback;
    private SensorManager mSensorManager;
    private StepCount mStepCount = new StepCount();
    private StepDetector mStepDetector  = new StepDetector();
    private PowerManager.WakeLock wakeLock;
    private SharedPreferences mSharePreference;
    private final static int GRAY_SERVICE_ID = 1001;
    private int totalDis = 0;

    public void onCreate() {
        super.onCreate();

        //防止休眠(喚醒鎖)
        getWakeLock();
        getSensor();
        mSharePreference = getSharedPreferences("data", Activity.MODE_PRIVATE);

    }

    @SuppressLint("InvalidWakeLockTag")
    private void getWakeLock() {
        wakeLock = ((PowerManager)(getSystemService(POWER_SERVICE))).newWakeLock(PARTIAL_WAKE_LOCK, "StepService");
        wakeLock.acquire(10*60*1000L);
    }

    private void getSensor() {
        //獲取感測器
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        assert mSensorManager != null;
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_UI);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final String date = year + "年" + month + "月" + day + "日";

        //步數改變
        mStepCount.initListener(new StepValuePassListener() {
            @Override
            public void stepChanged(int steps) {
                mSharePreference.edit().putString("steps", steps + "").apply();
                FirebaseDatabase.getInstance().getReference("步數").child(date).setValue(steps);
                int x = new Random().nextInt(10) + 80;
                totalDis = x + totalDis;
                int dis = totalDis / 100;
                mSharePreference.edit().putInt("dis", dis).apply();
                FirebaseDatabase.getInstance().getReference("距離").child(date).setValue(dis);
                mCallback.updateUi();
            }
        });
        mStepDetector.initListener(mStepCount);
    }

    //重置StepCount
    public void resetValues() {
        mSharePreference.edit().putString("steps","0").apply();
        mStepCount.setSteps(0);
    }

    //扣除步數
    public void game() {
        int newSteps = Integer.parseInt(mSharePreference.getString("steps", "")) - 100;
        mSharePreference.edit().putString("steps",String.valueOf(newSteps)).apply();
        mStepCount.setSteps(newSteps);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(mStepDetector);
        wakeLock.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }


    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Intent innerIntent = new Intent(this, GrayInnerService.class);
        startService(innerIntent);
        startForeground(GRAY_SERVICE_ID, new Notification());
        return START_STICKY;
    }

    public void registerCallback(UpdateUiCallBack paramICallback) {
        this.mCallback = paramICallback;
    }

    public boolean onUnbind(Intent paramIntent) {
        return super.onUnbind(paramIntent);
    }

    class StepBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }

    public static class GrayInnerService extends Service{
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
