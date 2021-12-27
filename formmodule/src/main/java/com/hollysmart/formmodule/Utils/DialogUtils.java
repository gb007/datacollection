package com.hollysmart.formmodule.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.hollysmart.formmodule.R;

public class DialogUtils {

    public DialogUtils() {
    }

    private ProgressDialog progressDialog;


    public void showProgress(Activity activity) {

        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(activity, null, activity.getString(R.string.form_model_progress_loading),
                    true, true);
        }

    }

    public void dismissProgress() {

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    public static void showDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", null);
        builder.create().show();
    }


}
