package com.fly.pedometer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SerachActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach);

        getSupportActionBar().hide();

        final DatePicker datePicker = findViewById(R.id.date);
        final TextView tvDate = findViewById(R.id.tv_date);
        final TextView tvStep = findViewById(R.id.tv_step);
        final TextView tvDis = findViewById(R.id.tv_dis);
        final TextView step = findViewById(R.id.step);
        final TextView distance = findViewById(R.id.distance);
        final Button btnOk = findViewById(R.id.ok);

        datePicker.setScaleX(1.3f);
        datePicker.setScaleY(1.3f);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.VISIBLE);
                tvDate.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
                tvStep.setVisibility(View.GONE);
                step.setVisibility(View.GONE);
                distance.setVisibility(View.GONE);
                tvDis.setVisibility(View.GONE);
            }
        });

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(final DatePicker datePicker, int year, int month, int day) {

                final String date = year + "年" + (month+1) + "月" + day + "日";
                FirebaseDatabase.getInstance().getReference("步數").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            tvDate.setText(date);
                            datePicker.setVisibility(View.GONE);
                            btnOk.setVisibility(View.VISIBLE);
                            tvDate.setVisibility(View.VISIBLE);
                            step.setVisibility(View.VISIBLE);
                            distance.setVisibility(View.VISIBLE);
                            tvStep.setVisibility(View.VISIBLE);
                            tvDis.setVisibility(View.VISIBLE);
                            tvStep.setText(dataSnapshot.getValue() + "步");
                        }else{
                            new AlertDialog.Builder(SerachActivity.this)
                                    .setMessage("沒有步數的紀錄")
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                FirebaseDatabase.getInstance().getReference("距離").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            tvDate.setText(date);
                            datePicker.setVisibility(View.GONE);
                            btnOk.setVisibility(View.VISIBLE);
                            tvDate.setVisibility(View.VISIBLE);
                            step.setVisibility(View.VISIBLE);
                            distance.setVisibility(View.VISIBLE);
                            tvStep.setVisibility(View.VISIBLE);
                            tvDis.setVisibility(View.VISIBLE);
                            tvDis.setText(dataSnapshot.getValue() + "m");
                        }else{
                            new AlertDialog.Builder(SerachActivity.this)
                                    .setMessage("沒有距離的紀錄")
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
