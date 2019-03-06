package com.fly.pedometer.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuestNum {

    private int[] guestNum = new int[4];
    private List<Integer> nums = new ArrayList<>();
    String num = "";

    public GuestNum() {
        create();
        setNum();
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void create() {
        //把0~9丟入
        for (int i = 0; i < 10; i++) {
            nums.add(i);
        }
        //打亂
        Collections.shuffle(nums);

        for (int i = 0; i < 4; i++) {
            guestNum[i] = Integer.parseInt(nums.get(i).toString());
        }
    }

    public void setNum(){
        if (guestNum[0] != 0) {
            for (int i = 0; i < guestNum.length; i++) {
                num +=String.valueOf(guestNum[i]);
            }
            setNum(num);
        } else {
            guestNum[0] = Integer.parseInt(nums.get(4).toString());
            for (int i = 0; i < guestNum.length; i++) {
                num +=String.valueOf(guestNum[i]);
            }
            setNum(num);
        }
    }

    public String getNum(){
        return num;
    }

}
