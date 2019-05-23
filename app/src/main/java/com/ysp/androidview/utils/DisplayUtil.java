package com.ysp.androidview.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

public class DisplayUtil {

    public static int dpToPx(Context context, float dp){
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

   public static int spToPx(Context context,float sp){
        return (int) (TypedValue.applyDimension(2, sp, context.getResources().getDisplayMetrics()) + 0.5f);
    }
    /**
     * 测量文字宽高
     */
    public static Float[] measureTextSize(Paint paint , String text){
        if (TextUtils.isEmpty(text))
            return new Float[]{0f,0f};
        float width = paint.measureText(text, 0, text.length());
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return new Float[]{width, Float.valueOf(bounds.height())};
    }
    /***
     * 获取窗口的宽度
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context){
        int width = 0;
        if(context instanceof Activity){
            Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            d.getMetrics(dm);
            width = dm.widthPixels;
        }
        return width;
    }

    /***
     * 获取窗口的高度
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context){
        int height = 0;
        if(context instanceof Activity){
            Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            d.getMetrics(dm);
            height = dm.heightPixels;
        }
        return height;
    }
}
