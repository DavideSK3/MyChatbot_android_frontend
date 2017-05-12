package com.example.mychatbot.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * Created by david on 06/05/2017.
 *
 * Utility methods to modify strings/values
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

    public static String getTodayDate(){
        String now = new Timestamp(System.currentTimeMillis()).toString();
        String today = now.substring(0, 10);
        return today;
    }

    public static String getTomorrow(String today){
        String year = today.substring(0,4);
        int month=Integer.parseInt(today.substring(5,7));
        int day= Integer.parseInt(today.substring(8,10));
        System.out.println("tomorrow of: *"+year+"*"+month+"*"+day);
        if(month==06){
            if(day==30){
                return year +"-07-01";
            } else {
                if(day>=9){
                    return year+"-"+String.valueOf(month)+"-"+String.valueOf(day+1);
                } else {
                    return year+"-"+String.valueOf(month)+"-0"+String.valueOf(day+1);
                }
            }
        } else {
            if(day==31){
                if(month>=9){
                    return year +"-"+String.valueOf(month+1)+"-01";
                } else {
                    return year +"-0"+String.valueOf(month+1)+"-01";
                }
            } else {
                if(month>=9){
                    if(day>=9){
                        return year+"-"+String.valueOf(month)+"-"+String.valueOf(day+1);
                    } else {
                        return year+"-"+String.valueOf(month)+"-0"+String.valueOf(day+1);
                    }
                } else {
                    if(day>=9){
                        return year+"-0"+String.valueOf(month)+"-"+String.valueOf(day+1);
                    } else {
                        return year+"-0"+String.valueOf(month)+"-0"+String.valueOf(day+1);
                    }
                }
            }

        }
    }

    public static String getYesterday(String today){
        String year = today.substring(0,4);
        int month=Integer.parseInt(today.substring(5,7));
        int day= Integer.parseInt(today.substring(8,10));
        System.out.println("yesterday of: *"+year+"*"+month+"*"+day);
        if(month==07){
            if(day==01){
                return year +"-06-30";
            } else {
                if(day>=11){
                    return year+"-"+String.valueOf(month)+"-"+String.valueOf(day-1);
                } else {
                    return year+"-"+String.valueOf(month)+"-0"+String.valueOf(day-1);
                }
            }
        } else {
            if(day==01){
                if(month>=11){
                    return year +"-"+String.valueOf(month-1)+"-31";
                } else {
                    return year +"-0"+String.valueOf(month-1)+"-31";
                }
            } else {
                if(month>=11){
                    if(day>=11){
                        return year+"-"+String.valueOf(month)+"-"+String.valueOf(day-1);
                    } else {
                        return year+"-"+String.valueOf(month)+"-0"+String.valueOf(day-1);
                    }
                } else {
                    if(day>=11){
                        return year+"-0"+String.valueOf(month)+"-"+String.valueOf(day-1);
                    } else {
                        return year+"-0"+String.valueOf(month)+"-0"+String.valueOf(day-1);
                    }
                }
            }

        }
    }
}
