package com.doan.timnhatro.utils;

import java.util.Calendar;

public class DateUtils {

    public static String getTimeCount(String millisecond){

        String mTimer = "";

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        long diff = now - Long.valueOf(millisecond);

        int seconds = (int) (diff / 1000) % 60 ;
        int minutes = (int) ((diff / (1000*60)) % 60);
        int hours   = (int) ((diff / (1000*60*60)) % 24);
        int days = (int) (diff / (1000*60*60*24));

        if (seconds==0&&minutes==0&&hours==0&days==0){
            mTimer = "Vừa xong";
        }else if (seconds>0&&minutes==0&&hours==0&days==0){
            mTimer = seconds + " giây trước";
        }else if (minutes>0&&hours==0&days==0){
            mTimer = minutes + " phút trước";
        }else if (hours>0&days==0){
            mTimer = hours + " giờ trước";
        }else if (days>0&&days<31){
            mTimer = days + " ngày trước";
        }else if (days>30&&days<61) {
            mTimer = "1 tháng trước";
        }else if (days>60&&days<91) {
            mTimer = "2 tháng trước";
        }else if (days>90&&days<121) {
            mTimer = "3 tháng trước";
        }else if (days>120&&days<151) {
            mTimer = "4 tháng trước";
        }else if (days>150&&days<181) {
            mTimer = "5 tháng trước";
        }else if (days>180&&days<211) {
            mTimer = "6 tháng trước";
        } else if (days>210&&days<241) {
            mTimer = "7 tháng trước";
        } else if (days>240&&days<271) {
            mTimer = "8 tháng trước";
        } else if (days>270&&days<301) {
            mTimer = "9 tháng trước";
        } else if (days>300&&days<331) {
            mTimer = "10 tháng trước";
        } else if (days>330&&days<361) {
            mTimer = "11 tháng trước";
        } else if (days>360&&days<366) {
            mTimer = "12 tháng trước";
        }else if (days>365){
            mTimer = "1 năm trước";
        }

        return mTimer;
    }
}
