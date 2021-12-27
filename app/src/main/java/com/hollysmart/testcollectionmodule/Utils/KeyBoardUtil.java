package com.hollysmart.testcollectionmodule.Utils;


import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘事件Util
 */
public class KeyBoardUtil {



    /**
     * 键盘显示则隐藏键盘
     */
    public  static void hiddenKeyBoard(Activity activity){
        View v = activity.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
