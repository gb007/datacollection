package com.hollysmart.formmodule.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单正则规则类
 */
public  class RegularExpressionUtils {

    /**手机号码*/
    public static final String MOBILE_KEY = "m";
    /**邮政编码*/
    public static final String POSTAL_KEY = "p";
    /**字母*/
    public static final String LETTER_KEY = "s";
    /**数字，数字包含正整数、负整数、正小数、负小数、零*/
    public static final String NUMBER_KEY = "n";
    /**整数 （整数在前端判断，这里不用写正则） */
    public static final String INTEGER_KEY = "z";
    /**邮箱地址*/
    public static final String EMAIL_KEY = "e";
    /** 金额 */
    public static final String MONEY_KEY = "money";
    /**网址*/
    public static final String URL_KEY = "url";


    public static RegularExpressionUtils instatce;
    private List<Regular> regularList;

    public RegularExpressionUtils() {
        regularList = new ArrayList<>();
        setExpressionList(regularList);
    }

    public static RegularExpressionUtils getInstatce() {
        if(null == instatce){
            instatce = new RegularExpressionUtils();
        }
        return instatce;
    }

    public List<Regular> getRegularList() {
        return regularList;
    }

    public void setRegularList(List<Regular> regularList) {
        this.regularList = regularList;
    }

    private void setExpressionList(List<Regular> regularList){

        /**唯一*/
        Regular only = new Regular("only","only","");
        /**6-16位的数字*/
        Regular num6_16 = new Regular("n6-16","^\\d{6,16}$","请输入6-16位的数字");
        /**6-16位任意字符*/
        Regular string6_16 = new Regular("*6-16","^.{6,16}$","请输入6-16位任意字符");
        /**6-18位字母*/
        Regular letter6_18 = new Regular("s6-18","^[a-z|A-Z]{6,18}$","请输入6-18位字母");
        /**网址*/
        Regular URL = new Regular("url","^((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:\\/~+#]*[\\w\\-@?^=%&\\/~+#])?$","请输入正规的网址");
        /**手机号码*/
        Regular mobile = new Regular("m","^1[3456789]\\d{9}$","请输入正规的手机号码");
        /**邮政编码*/
        Regular postal = new Regular("p","^[1-9]\\d{5}$","请输入正规的邮政编码");
        /**字母*/
        Regular letter = new Regular("s","[A-Z|a-z]+$","请输入字母");
        /**数字，数字包含正整数、负整数、正小数、负小数、零*/
        Regular number = new Regular("n","^-?\\d+(\\.?\\d+|\\d?)$","请输入数字");
        /**整数 （整数在前端判断，这里不用写正则） */
        Regular integer = new Regular("z","z","请输入整数");
        /**非空*/
        Regular notnull = new Regular("*", "^.+$", "该字段不能为空");
        /**邮箱地址*/
        Regular email = new Regular("e","^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$","请输入正确格式的邮箱地址");
        /** 金额 */
        Regular money = new Regular("money","^(([1-9][0-9]*)|([0]\\.\\d{0,2}|[1-9][0-9]*\\.\\d{0,5}))$","请输入正确的金额");

        regularList.add(only);
        regularList.add(num6_16);
        regularList.add(string6_16);
        regularList.add(letter6_18);
        regularList.add(URL);
        regularList.add(mobile);
        regularList.add(postal);
        regularList.add(letter);
        regularList.add(number);
//        regularList.add(integer);
        regularList.add(notnull);
        regularList.add(email);
        regularList.add(money);

    }


   public class Regular{

        private String key;
        private String tip;
        private String expression;

        public Regular(String key, String expression,String tip) {
            this.key = key;
            this.tip = tip;
            this.expression = expression;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

}
