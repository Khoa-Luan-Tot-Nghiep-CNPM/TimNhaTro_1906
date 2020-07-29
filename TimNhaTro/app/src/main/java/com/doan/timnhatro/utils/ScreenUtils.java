package com.doan.timnhatro.utils;

import android.graphics.Point;
import android.view.WindowManager;
import com.doan.timnhatro.base.BaseApplication;

import static android.content.Context.WINDOW_SERVICE;

public class ScreenUtils {

    private static ScreenUtils screenUtils;

    public static ScreenUtils getInstance(){
        if (screenUtils == null){
            screenUtils = new ScreenUtils();
        }
        return screenUtils;
    }

    public int getWidth(){
        Point size = new Point();
        WindowManager mWindowManager = (WindowManager) BaseApplication.getContext().getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.getDefaultDisplay().getSize(size);
        }
        return size.x;
    }
    public int getHeight(){
        Point size = new Point();
        WindowManager mWindowManager = (WindowManager) BaseApplication.getContext().getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.getDefaultDisplay().getSize(size);
        }
        return size.y;
    }
}