package com.yaya.common.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/6
 * @description 验证字段
 */
public class ValidatorUtil {

    /**
     * 校验参数方法.
     *
     * @param destStr    需要校验的目标字符串
     * @param fieldName  错误信息绑定的属性名
     * @param isRequired 是否必填
     * @param minLength  最小长度
     * @param maxLength  最大长度
     * @param dataType  1.字符串为数字, 2. 字符串为“0”---”9”, ”a”---”z”,”A”---”Z” ； 3.字符串为values中的值 4.任意字符串 5.IP 6.Email地址
     * @param errorMap   错误信息Map
     * @return int 0:success, other failure
     * @version
     */
    public static int validParameterAlert(String destStr, String fieldName, boolean isRequired,
                                   int minLength, int maxLength, int dataType, Map<String,String> errorMap) {
        int returnCode = 0;
        try {
            returnCode = ValidatorUtil.validParameter(destStr, isRequired, minLength, maxLength, dataType, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /**
         * 1：必填但没填写 
         * 2:不符合要求的长度范围，限制最小长度 
         * 3:包含单双引号 
         * 4:非数字 
         * 5:不符合handleType为2所要求的值 
         * 6:不符合handleType为3所要求的值 
         * 7:不符合要求的ip地址 
         * 8:大于要求的最大长度，不限制最小长度 
         * 9:不符合要求的email地址
         */
        switch (returnCode) {
            case 0:
                return returnCode;
            case 1:
                errorMap.put(fieldName,  "请填写");
                break;
            case 2:
                if (minLength != maxLength) {
                    errorMap.put(fieldName, "不能少于" + minLength
                            + "且不能超过" + maxLength + "个字符");
                } else {
                    errorMap.put(fieldName, "应该为" + minLength + "个字符");
                }
                break;
            case 3:
                errorMap.put(fieldName, "不允许带有单引号或双引号");
                break;
            case 4:
                errorMap.put(fieldName, "必须是数字");
                break;
            case 5:
                errorMap.put(fieldName, "不能超过" + maxLength + "只能是数字和字母");
                break;
            case 6:// no this
                break;
            case 7:
                errorMap.put(fieldName, "请填写"  + "正确的IP地址");
                break;
            case 8:
                errorMap.put(fieldName, "不能少于" + maxLength + "个字符");
                break;
            case 9:
                errorMap.put(fieldName, "请填写"  + "正确的电子邮箱地址");
                break;
            default:
                returnCode = 99;
                break;
        }
        return returnCode;
    }

    /**
     * 预处理参数，默认值、长度等
     *
     * @param destStr   参数
     * @param required  必填
     * @param minLength 是已经填写了值的情况下的参数最小参数，即最少填写1
     * @param maxLength 参数最大长度
     * @param dataType  数据类型 1.字符串为数字, 2. 字符串为“0”---”9”, ”a”---”z”,”A”---”Z” ；            3.字符串为values中的值 4.任意字符串 5.IP
     * @param values    参数列表值
     * @return int 0：ok
     *              1：必填但没填写
     *              2:不符合要求的长度范围，限制最小长度
     *              3:包含单双引号
     *              4:非数字
     *              5:不符合handleType为2所要求的值
     *              6:不符合handleType为3所要求的值
     *              7:不符合要求的ip地址
     *              8:大于要求的最大长度，不限制最小长度
     *              9:不符合要求的email地址
     * @throws Exception the exception
     */
    public static int validParameter(String destStr, boolean required, int minLength, int maxLength, int dataType, String[] values) throws Exception {
        int returnCode = 0;

        // 判断是否必填
        if (required) {
            if (destStr == null || destStr.length() == 0 || "".equals(destStr.trim())) {
                return 1;
            }
        } else {
            if (destStr == null || destStr.length() == 0 || "".equals(destStr.trim())) {
                return 0;
            }
        }

        // check length
        int len = getCharLength(destStr);
        if (len == -1) {
            throw new Exception("Get string length error");
        }
        if (minLength > 0) {
            if (len < minLength || len > maxLength) {
                return 2;
            }
        } else {
            if (len > maxLength) {
                return 8;
            }
        }
        // check whether contains ' and " character
        if (destStr.indexOf('\'') >= 0 || destStr.indexOf('\"') >= 0) {
            return 3;
        }

        switch (dataType) { // 1.字符串为数字 ； 2. 字符串为“0”---”9”, ”a”---”z”,
        // ”A”---”Z” ；3 .字符串为values中的值 4.任意字符串
        case 1:
            if (!isNumeric(destStr)) {
                returnCode = 4;
            }
            break;
        case 2:
            if (!isNumOrChar(destStr)) {
                returnCode = 5;
            }
            break;
        case 3:
            returnCode = 6;
            if (values == null) {
                break;
            }
            for (int i = 0; i < values.length; i++) {
                String tmp = values[i];
                if (destStr.toLowerCase().equals(tmp.toLowerCase())) { // 都转化为小写进行比较
                    returnCode = 0;
                    break;
                }
            }
            break;
        case 4:
            break;
        case 5:
            if (!isIp(destStr)) {
                returnCode = 7;
            }
            break;
        case 6:
            if (!isEmail(destStr)) {
                returnCode = 9;
            }
            break;
        default:
            returnCode = 99;
            break;
        }
        return returnCode;
    }

    /**
     * 正则表达式验证
     *
     * @param destStr 被检查字符串
     * @param regex   正则表达式
     * @return boolean ，true_符合正则表达式
     */
    public static boolean isRegex(String destStr, String regex) {
        if (destStr == null || destStr.trim().length() == 0) {
            return false;
        }
        return destStr.matches(regex);
    }

    /**
     * 数字验证
     *
     * @param destStr 被检查字符串
     * @return boolean ，true_数字
     */
    public static boolean isNumeric(String destStr) {
        return destStr.matches("\\d+");
    }

    /**
     * 邮箱验证
     *
     * @param destStr 被检查字符串
     * @return boolean ，true_邮箱
     */
    public static boolean isEmail(String destStr) {
        return destStr.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    /**
     * 字符串是否由0--9,a--z,A--Z组成
     *
     * @param destStr 被检查字符串
     * @return boolean ，true_字符串由0--9,a--z,A--Z组成
     */
    public static boolean isNumOrChar(String destStr) {
        return destStr.matches("\\w+");
    }

    /**
     * 得到字符的长度，统一用UTF-8，汉字算三
     *
     * @param tmp string
     * @return int string length -1_表示返回错误，其他为字段长度
     */
    public static int getCharLength(String tmp) {
        int len = 1;
        try {
            len = tmp.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            len = -1;
        }
        return len;
    }

    /**
     * IP检验
     *
     * @param ip ip address
     * @return boolean true:yes false:no
     */
    public static boolean isIp(String ip) {
        // TODO Auto-generated method stub
        return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|23[0-2])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
    }

    /**
     * 判断字符串是否为合法手机号 11位
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isMobilePhone(String str){
        if(isEmpty(str)){
            return false;
        }
        return str.matches("^1\\d{10}$");
    }

    /**
     * 判断字符串是否为合法座机号
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isTelPhone(String str){
        if(isEmpty(str)){
            return false;
        }
        return str.matches("^0((\\d{2}-\\d{8}|(\\d{3}-\\d{7})))$");
    }

    /**
     * 判断是否为数字
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isNumber(String str) {
        try{
            Integer.parseInt(str);
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    /**
     * 判断字符串是否为非空(包含null与"")
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isNotEmpty(String str){
        if(str == null || "".equals(str))
            return false;
        return true;
    }

    /**
     * 判断字符串是否为非空(包含null与"","    ")
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isNotEmptyIgnoreBlank(String str){
        if(str == null || "".equals(str) || "".equals(str.trim()))
            return false;
        return true;
    }

    /**
     * 判断字符串是否为空(包含null与"")
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isEmpty(String str){
        if(str == null || "".equals(str))
            return true;
        return false;
    }

    /**
     * 判断字符串是否为空(包含null与"","    ")
     *
     * @param str the str
     * @return boolean boolean
     */
    public static boolean isEmptyIgnoreBlank(String str){
        if(str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

    /**
     * 判断是否为浮点数或者整数
     *
     * @param str the str
     * @return true Or false
     */
    public static boolean isNumberOrFloat(String str){
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 验证字符串是否是时间格式.
     *
     * @param dateStr 时间字符串
     * @param format  时间格式 默认为：yyyy-MM-dd HH:mm:ss
     * @return 返回值 true:是时间格式  false:不是时间格式
     */
    public static boolean isValidDate(String dateStr, String format)
    {
        if(dateStr == null || dateStr.trim().length() == 0) {
            return false;
        }
        if(format == null || format.trim().length() == 0) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            df.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //禁止实例化
    private ValidatorUtil(){}
}
