package com.king.app.tcareer.utils;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/24 13:19
 */
public class FormatUtil {

    public static String formatNumber(double money) {
        return formatNumber(String.valueOf(money));
    }
    /**
     * 格式化字符串，去掉末尾多余的0
     *
     * @param money
     * @return
     */
    public static String formatNumber(String money) {
        StringBuffer buffer = new StringBuffer(money);
        if (money.contains(".")) {
            for (int i = money.length() - 1; i > 1; i--) {
                if (buffer.charAt(i) == '0') {
                    buffer.deleteCharAt(i);
                    if (buffer.charAt(i - 1) == '.') {
                        buffer.deleteCharAt(i - 1);
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        return buffer.toString();
    }

}
