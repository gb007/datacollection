package com.hollysmart.formmodule.activity;

import android.content.Intent;
import android.view.View;

import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.common.Constants;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.lwkandroid.imagepicker.data.ImagePickerCropParams;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cai on 16/7/28
 */
public class AddPicActivity extends CaiBaseActivity {


    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_add_pic;
    }


    @Override
    public void findView() {
    }
    private int num;
//    private FormFiledBean bean;
      private List<PicBean> pics;
      private String dbFieldName;
    private String dataJson;

    @Override
    public void init() {
        num = getIntent().getIntExtra("num", 9);
//      bean = (FormFiledBean) getIntent().getSerializableExtra("bean");
        pics = (List<PicBean>) getIntent().getSerializableExtra("pics");
        dbFieldName = getIntent().getStringExtra("dbFieldName");
        dataJson = getIntent().getStringExtra("dataJson");
        new ImagePicker()
                .pickType(ImagePickType.MULTI)
                .maxNum(num)
                .needCamera(true)
                .cachePath(getCacheDir().getPath())
                .doCrop(new ImagePickerCropParams())
                .start(this, Constants.REQUEST_CODE_IMAGE);


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (pics != null)
                data.putExtra("pics", (Serializable)pics);
            data.putExtra( "dataJson", dataJson );
            data.putExtra( "dbFieldName", dbFieldName );
            setResult(2, data);
            finish();
        }else {
            finish();
        }


    }
}





















