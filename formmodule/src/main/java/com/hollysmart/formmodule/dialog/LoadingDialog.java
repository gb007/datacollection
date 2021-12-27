package com.hollysmart.formmodule.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hollysmart.formmodule.R;


public class LoadingDialog extends Dialog {

    private Context mContext;
    private String message;

    public LoadingDialog(@NonNull Context context, int themeResId, String message) {
        super(context, themeResId);
        this.mContext = context;
        this.message = message;
    }




    private TextView tv_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mView = LayoutInflater.from(mContext).inflate(R.layout.form_module_dialog_loading, null);
        setContentView(mView);
        tv_message = mView.findViewById( R.id.tv_message);
        tv_message.setText(message);
    }

}
