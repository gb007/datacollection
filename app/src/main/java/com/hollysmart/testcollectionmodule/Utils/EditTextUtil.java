package com.hollysmart.testcollectionmodule.Utils;

import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.hjq.toast.ToastUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 设置输入框的输入类型以及校验输入内容是否符合正则
 */
public class EditTextUtil {


    /**
     * 普通文本设置EditText输入框类型
     *
     * @param et_value         EditText控件
     * @param field_vaild_type inputType类型的key
     */
    public static void setInputType(EditText et_value, String field_vaild_type) {
        //  1：数字 2：英文和数字 3：邮箱 4：电话号码和固话 5：身份证号 6：ip地址 7：邮政编码 8：数字和小数点 9:字母
        if (RegularExpressionUtils.INTEGER_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if ("2".equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else if (RegularExpressionUtils.EMAIL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (RegularExpressionUtils.MOBILE_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        } else if ("5".equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        } else if (RegularExpressionUtils.URL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        } else if (RegularExpressionUtils.POSTAL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        } else if (RegularExpressionUtils.MONEY_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (RegularExpressionUtils.LETTER_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }else{
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }

    }


    /**
     * 多行文本设置EditText输入框类型
     *
     * @param et_value
     * @param field_vaild_type
     */
    public static void setMultiInputType(EditText et_value, String field_vaild_type) {
        //  1：数字 2：英文和数字 3：邮箱 4：电话号码和固话 5：身份证号 6：ip地址 7：邮政编码 8：数字和小数点 9:字母
        if (RegularExpressionUtils.INTEGER_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if ("2".equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.EMAIL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.MOBILE_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if ("5".equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.URL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_TEXT_VARIATION_URI| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.POSTAL_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.MONEY_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (RegularExpressionUtils.LETTER_KEY.equals(field_vaild_type)) {
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }else{
            et_value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }

    }


    /**
     * 校验Text内容是否符合正则规则
     *
     * @param par         正则规则的key
     * @param text        需要验证的内容
     * @param regularList 正则List
     * @param showTips    不符合正则规则时是否谈提示
     * @return
     */
    public static boolean matachText(String par, String text, List<RegularExpressionUtils.Regular> regularList, boolean showTips) {

        for (RegularExpressionUtils.Regular regular : regularList) {
            if (regular.getKey().equals(par)) {
                String expression = regular.getExpression();
                Pattern p = Pattern.compile(expression);
                if (!TextUtils.isEmpty(text)) {
                    Matcher m = p.matcher(text);
                    if (!m.matches()) {
                        if (showTips) {
                            ToastUtils.show(regular.getTip());
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }


}
