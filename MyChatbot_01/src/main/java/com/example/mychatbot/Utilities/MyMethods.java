package com.example.mychatbot.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * Created by david on 06/05/2017.
 */

public class MyMethods {

    public MyMethods(){

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getTimeString(String s){
        String now = new Timestamp(System.currentTimeMillis()).toString();
        String today = now.substring(0, 11);
        String day = s.substring(0, 11);
        //System.out.println(now+"  "+today+"  "+day);
        if (today.equals(day)){
            return s.substring(11,16);
        }
        return day;
    }
}
