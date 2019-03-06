package com.fly.pedometer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.pedometer.Utils.GuestNum;

public class GuestActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] ans;
    private EditText  etInput;
    private String num;
    private int totalScore;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        setTitle("1A2B遊戲室");

        findViews();

        getGuestNum();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                ngAnswer();
                break;
            case R.id.tv_answer:
                new AlertDialog.Builder(this).setMessage(num).show();
                break;
        }
    }

    private void getGuestNum() {
        GuestNum guestNum = new GuestNum();
        num = guestNum.getNum();

        //把正確答案放到陣列
        ans = new int[4];
        for (int i = 0; i < 4; i++) {
            ans[i] = Integer.parseInt(num.substring(i, i + 1));
        }
    }

    private void findViews() {
        etInput = findViewById(R.id.et_input);
        ll = findViewById(R.id.ll);
        TextView answer = findViewById(R.id.tv_answer);
        Button btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        answer.setOnClickListener(this);
    }


    @SuppressLint("SetTextI18n")
    public void ngAnswer() {
        int A = 0;
        int B = 0;
        int[] inp = new int[4];
        String guestNum = etInput.getText().toString();

        //把輸入鍵盤縮下去
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(),0);

        //判斷是否4位數
        if (guestNum.length() == 4) {
            //判斷輸入是否為數字
            try {
                for (int i = 0; i < 4; i++) {
                    inp[i] = Integer.parseInt(guestNum.substring(i, i + 1));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "輸入錯誤,自動清空", Toast.LENGTH_SHORT).show();
                etInput.setText("");
                return;
            }

            //判斷A和B
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (ans[i] == inp[j]) if (i == j) {
                        A++;
                    } else {
                        B++;
                    }
                }
            }

            //列出幾A幾B
            if (A < 4) {
                TextView textView = new TextView(this);
                textView.setTextColor(Color.BLUE);
                textView.setTextSize(25);
                textView.setText("\t" + "你的數字:" + guestNum + "        " +"結果" + A + "A" + B + "B");
                ll.addView(textView);
            } else {
                Toast.makeText(GuestActivity.this, "恭喜過關", Toast.LENGTH_SHORT).show();
                int winScore = 10;
                totalScore = totalScore + winScore;
                getSharedPreferences("game", MODE_PRIVATE).edit().putInt("score", totalScore).apply();
                new AlertDialog.Builder(GuestActivity.this)
                        .setTitle("過關")
                        .setMessage("恭喜答對了")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        } else {
            Toast.makeText(this, "長度只限4位數", Toast.LENGTH_SHORT).show();
            etInput.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        //判斷沒答對 不小心按到返回
        if (!num.equals(etInput.getText().toString().trim())) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("遊戲還沒結束,確定要離開嗎").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("取消",null).show();
        }
    }
}

