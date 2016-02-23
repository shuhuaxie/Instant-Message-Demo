package androidLearn.frame.easemobexample.utils;

import java.util.List;

import hanyu.pinyin.HanyuPinyinHelper;


public class PinYin {
    private static HanyuPinyinHelper helper = new HanyuPinyinHelper();
    
    public static List<String> getPinYin(String str){
        return helper.hanyuPinYinConvert(str);
    }
}
