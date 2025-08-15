package com.fatebug.base.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * <p>
 * 将中文转换为拼音
 * </p>
 *
 * @author fatebug
 */
public class PingYinUtil {

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param chinese 传入中文
     */
    public static String getPingYin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
       /* 设置转换后拼音的大小写
             UPPERCASE：大写  (ZHONG)
            LOWERCASE：小写  (zhong)
        */
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
      /* 设置转换后拼音是否输出拼接音调
            WITHOUT_TONE：无音标  (zhong)
            WITH_TONE_NUMBER：1-4数字表示英标  (zhong4)
            WITH_TONE_MARK：直接用音标符（必须WITH_U_UNICODE否则异常）  (zhòng)
       */
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
      /* 设置转换后拼音是否输出韵母
            WITH_V：用v表示ü  (nv)
            WITH_U_AND_COLON：用"u:"表示ü  (nu:)
            WITH_U_UNICODE：直接用ü (nü)
       */
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        StringBuilder sb = new StringBuilder();
        char[] arr = chinese.trim().toCharArray();
        try {
            for (char c : arr) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    //toHanyuPinyinStringArray有个容错判断 如果传入的不是汉字，就不能转换成拼音，那么直接返回null可以用正则表达式判断是否是中文,Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    sb.append(temp[0]);
                    continue;
                }

                sb.append(c);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @param type    小于等于0：取汉字串拼音首字母；大于0：获取汉字串全拼音
     * @return 汉语拼音首字母
     */
    public static String getPingYin(String chinese, int type) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
       /* 设置转换后拼音的大小写
             UPPERCASE：大写  (ZHONG)
            LOWERCASE：小写  (zhong)
        */
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
      /* 设置转换后拼音是否输出拼接音调
            WITHOUT_TONE：无音标  (zhong)
            WITH_TONE_NUMBER：1-4数字表示英标  (zhong4)
            WITH_TONE_MARK：直接用音标符（必须WITH_U_UNICODE否则异常）  (zhòng)
       */
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        char[] arr = chinese.trim().toCharArray();
        try {
            for (char c : arr) {
                //ASCII码表上一个128个字符,对应的索引为0-127,大于128表示非ASCII字符
                if (c > 128) {
                    //toHanyuPinyinStringArray有个容错判断 如果传入的不是汉字，就不能转换成拼音，那么直接返回null可以用正则表达式判断是否是中文,Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (temp != null) {
                        //输出全拼音
                        if (type <= 0) {
                            sb.append(PinyinHelper.toHanyuPinyinStringArray(c, format)[0]);
                        }
                        //输出拼音首字母
                        else {
                            sb.append(temp[0].charAt(0));
                        }
                    }
                    continue;
                }
                sb.append(c);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sb.toString().replaceAll("\\W", "").trim();
    }
}
