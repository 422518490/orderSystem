package com.yaya.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description AccessToken生成类
 */
public class AccessTokenUtil {

    public static String createAccessToken(String userId,String timestamp){
        String[] paramArr = new String[]{userId,timestamp};
        Arrays.sort(paramArr);
        String content = paramArr[0].concat(paramArr[1]);
        String access_token = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            access_token = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return access_token;
    }

    private static String byteToStr(byte[] byteArray){
        String strDigest="";
        for(int i=0;i<byteArray.length;i++){
            strDigest +=byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static String byteToHexStr(byte mByte){
        char[] Digit = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }
}
