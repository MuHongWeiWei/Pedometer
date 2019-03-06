package com.fly.pedometer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fly.pedometer.Interface.UpdateUiCallBack;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_step,tvDistance,tvDate;
    private StepService mService;
    private boolean mIsRunning;
    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        getTodayDate();
        startStepService();
    }

    protected void onResume() {
        super.onResume();

        String step = mySharedPreferences.getString("steps", "0");
        tv_step.setText(String.format("%s步", step));

        int distance = mySharedPreferences.getInt("dis", 0);
        tvDistance.setText(distance + "m");

        if (mIsRunning) {
            bindStepService();
        }
    }

    //開啟服務
    private void startStepService() {
        mIsRunning = true;
        startService(new Intent(this, StepService.class));
    }

    private void bindStepService() {
        bindService(new Intent(this, StepService.class), mConnection, BIND_AUTO_CREATE);
    }

    //Activity與Service連結
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService.StepBinder binder = (StepService.StepBinder) service;
            mService = binder.getService();
            mService.registerCallback(mUiCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //回傳服務資料 用handler更新UI
    private UpdateUiCallBack mUiCallback = new UpdateUiCallBack() {
        @Override
        public void updateUi() {
            Message message = mHandler.obtainMessage();
            message.what = 1;
            mHandler.sendMessage(message);



        }
    };

    //更新UI
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String steps = mySharedPreferences.getString("steps", "0");
                tv_step.setText(String.format("%s步", steps));

                int distance = mySharedPreferences.getInt("dis", 0);
                tvDistance.setText(distance + "m");
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_serach:
                startActivity(new Intent(this, SerachActivity.class));
                break;
            case R.id.btn_reset:
                new AlertDialog.Builder(MainActivity.this).setTitle("確定要重置嗎?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mService.resetValues();
                        tv_step.setText(mySharedPreferences.getString("steps", "0") + "步");
                    }
                }).show();
                break;
            case R.id.btn_game:
                String steps = mySharedPreferences.getString("steps", "0");
                if (Integer.parseInt(steps) >= 100) {
                    mService.game();
                    tv_step.setText(mySharedPreferences.getString("steps", "0") + "步");
                    startActivity(new Intent(MainActivity.this, GuestActivity.class));
                } else {
                    new AlertDialog.Builder(this).setTitle("低於100步無法進入遊戲").setCancelable(false).setPositiveButton("確定", null).show();
                }
                break;
        }
    }

    private void getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String date = year + "年" + month + "月" + day + "日";
        tvDate.setText(date);
    }

    public void findViews() {
        mySharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        tv_step = findViewById(R.id.step_tv);
        tvDistance = findViewById(R.id.tv_distance);
        tvDate = findViewById(R.id.tv_date);
        Button btn_reset = findViewById(R.id.btn_reset);
        Button btn_record = findViewById(R.id.btn_serach);
        Button btn_game = findViewById(R.id.btn_game);
        btn_game.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
    }

}