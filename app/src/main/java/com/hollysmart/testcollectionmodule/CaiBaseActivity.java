package com.hollysmart.testcollectionmodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.hollysmart.testcollectionmodule.common.Constants;
import com.hollysmart.testcollectionmodule.statusbar.StatusBarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

public abstract class CaiBaseActivity extends RxAppCompatActivity implements View.OnClickListener {
    public Context mContext;
    public int animType;
    //loading加载框
    private LoadingDialog loadingDialog;
    //防止多次点击记录上次点击事件
    //上次点击时间action_up时间
    private long exitTime = 0;
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (savedInstanceState != null) {
            animType = savedInstanceState.getInt(Constants.ANIM_TYPE);
        }
        animType = getIntent().getIntExtra(Constants.ANIM_TYPE, 0);

        setContentView(layoutResID());

        if (setTranslucent()) {
            //这里注意下 调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
            //是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
            //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
            StatusBarUtil.setRootViewFitsSystemWindows(this, false);
            //设置状态栏透明
            StatusBarUtil.setTranslucentStatus(this);
            StatusBarUtil.setStatusBarDarkTheme(this, false);
        }
        initLoadingDialog();
        findView();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * layout绑定
     */
    public abstract int layoutResID();

    /**
     * 设置状态栏透明
     *
     * @return
     */
    public abstract boolean setTranslucent();

    /**
     * 控件绑定
     */
    public abstract void findView();

    /**
     * 逻辑操作
     */
    public abstract void init();


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        animEnter(intent.getIntExtra(Constants.ANIM_TYPE, 0));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        animEnter(intent.getIntExtra(Constants.ANIM_TYPE, 0));
    }

    @Override
    public void finish() {
        super.finish();
        animExit();
    }

    private void animEnter(int animType) {
        switch (animType) {
            case Constants.ANIM_TYPE_SHANG:
                overridePendingTransition(R.anim.form_module_activity_enter_shang, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_XIA:
                overridePendingTransition(R.anim.form_module_activity_enter_xia, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_LEFT:
                overridePendingTransition(R.anim.form_module_activity_enter_left, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_RIGHT:
                overridePendingTransition(R.anim.form_module_activity_enter_right, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_SUOFANG:
                overridePendingTransition(R.anim.form_module_activity_enter_suofang, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_LONG_LEFT:
                overridePendingTransition(R.anim.form_module_activity_enter_long_left, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_LONG_RIGHT:
                overridePendingTransition(R.anim.form_module_activity_enter_long_right, R.anim.form_module_activity_yuandian);
                break;
            case Constants.ANIM_TYPE_ALPHA:
                overridePendingTransition(R.anim.form_module_activity_enter_alpha, R.anim.form_module_activity_yuandian);
                break;
        }
    }

    private void animExit() {
        switch (animType) {
            case Constants.ANIM_TYPE_SHANG:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_shang);
                break;
            case Constants.ANIM_TYPE_XIA:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_xia);
                break;
            case Constants.ANIM_TYPE_LEFT:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_left);
                break;
            case Constants.ANIM_TYPE_RIGHT:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_right);
                break;
            case Constants.ANIM_TYPE_SUOFANG:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_suofang);
                break;
            case Constants.ANIM_TYPE_LONG_LEFT:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_long_left);
                break;
            case Constants.ANIM_TYPE_LONG_RIGHT:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_long_right);
                break;
            case Constants.ANIM_TYPE_ALPHA:
                overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_alpha);
                break;
        }
    }

    /**
     * 初始化loadingDialog
     */
    private void initLoadingDialog() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText("加载中")
                .setSuccessText("加载成功")
                .setFailedText("加载失败")
                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                .closeSuccessAnim()
                .closeFailedAnim();
    }

    /**
     * 显示加载dialog
     */
    public void showLoadingDialog() {
        loadingDialog.setInterceptBack(false);//不拦截dialog显示时的返回事件
        loadingDialog.show();
    }

    /**
     * 显示加载dialog
     *
     * @param loadText    加载时提示文字
     * @param successText 加载成功提示文字
     * @param failedText  加载失败提示文字
     * @param interceptBack 是否拦截back，dialog不可外部设置消失
     */
    public void showLoadingDialog(String loadText, String successText, String failedText,boolean
            interceptBack) {
        loadingDialog
                .setInterceptBack(interceptBack)
                .setLoadingText(loadText)
                .setSuccessText(successText)
                .setFailedText(failedText);
        loadingDialog.show();
    }


    /**
     * 加载成功dialog展示
     */
    public void showDialogLoadSuccess() {
        loadingDialog.loadSuccess();
    }

    /**
     * 加载失败dialog展示
     */
    public void showDialogLoadFailed() {
        loadingDialog.loadFailed();
    }


    /**
     * 防止短时间内多处点击
     * 拦截屏幕触摸事件200ms内且X轴或者Y轴滑动小于25，则拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){//记录按下X Y位置
            x1 = ev.getX();
            y1 = ev.getY();
        }
        if(ev.getAction() == MotionEvent.ACTION_MOVE){//抬起X Y位置
            x2 = ev.getX();
            y2 = ev.getY();
        }
        if(ev.getAction() == MotionEvent.ACTION_UP){
            if (Math.abs((x1 - x2)) < 25 || Math.abs((y1 - y2)) < 25) {//X或Y轴移动超过25算移动不执行防点击判断
                if (isClick()) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击200ms秒内不能连续点击
     */
    public boolean isClick() {
        if ((System.currentTimeMillis() - exitTime) > Constants.EXITTIME) {
            exitTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

}










