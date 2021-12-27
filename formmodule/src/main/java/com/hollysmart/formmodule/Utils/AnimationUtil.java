package com.hollysmart.formmodule.Utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.hollysmart.formmodule.R;

/**
 * 动画工具类
 */
public class AnimationUtil {

    //Animation动画执行参数
    //X轴起始位置
    public static final float FROMXDELTA = -40;
    //X轴结束位置
    public static final float TOXDELTA = 40;
    //Y轴起始位置
    public static final float FROMYDELTA = 0;
    //Y轴起始位置
    public static final float TOYDELTA = 0;
    //动画单次持续时间
    public static final long DURATION = 80;
    //重复次数
    public static final int REPEATCOUNT = 2;


    /**
     * 背景变为红色，左右滑动
     *
     * @param context
     * @param view       执行抖动的view
     * @param fromXDelta X轴起始位置
     * @param toXDelta   X轴结束位置
     * @param fromYDelta Y轴起始位置
     * @param toYDelta   Y轴起始位置
     * @param duration   动画单次持续时间
     * @param repeatCount 重复次数
     */
    public static void startTranslateShakeByViewAnim(Context context, View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long duration, int repeatCount) {
        if (view == null) {
            return;
        }
        Animation translateAnim = new TranslateAnimation(fromXDelta,toXDelta,fromYDelta,toYDelta);
        translateAnim.setDuration(duration);
        translateAnim.setRepeatMode(Animation.REVERSE);
        translateAnim.setRepeatCount(repeatCount);
        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(translateAnim);

        translateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setBackgroundColor(context.getResources().getColor(R.color.form_module_red));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setBackgroundColor(context.getResources().getColor(R.color.form_module_white));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(smallAnimationSet);
    }




}
