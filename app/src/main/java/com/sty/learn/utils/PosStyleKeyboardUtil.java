package com.sty.learn.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.sty.learn.R;

/**
 * Created by shity on 2017/6/19/0019.
 */

public class PosStyleKeyboardUtil {

    /**
     * 显示输入键盘
     * @param context
     * @param softKeyboard
     */
    public static void show(Context context, FrameLayout softKeyboard){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_from_bottom);
        softKeyboard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        int visibility = softKeyboard.getVisibility();
        if(visibility == View.GONE || visibility == View.INVISIBLE){
            softKeyboard.clearAnimation();
            softKeyboard.startAnimation(animation);
            softKeyboard.setVisibility(View.VISIBLE);
        }
    }

    public static void hide(Context context, FrameLayout softKeyboard){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_to_bottom);
        int visibility = softKeyboard.getVisibility();
        if(visibility == View.VISIBLE){
            softKeyboard.clearAnimation();
            softKeyboard.startAnimation(animation);
            softKeyboard.setVisibility(View.INVISIBLE);
        }
    }
}
