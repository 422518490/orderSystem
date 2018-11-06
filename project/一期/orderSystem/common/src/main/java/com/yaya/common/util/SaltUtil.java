package com.yaya.common.util;

import java.util.Random;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/25
 * @description 盐值工具
 */
public class SaltUtil {

    public static final String createRandomCharData(int length) {
        StringBuilder sb = new StringBuilder();
        // 随机用以下三个随机生成器
        Random randomRange = new Random();
        Random randomData = new Random();
        int data = 0;
        for (int i = 0; i < length; i++) {
            int index = randomRange.nextInt(3);
            // 目的是随机选择生成数字，大小写字母
            switch (index) {
                case 0:
                    // 仅仅会生成0~9
                    data = randomData.nextInt(10);
                    sb.append(data);
                    break;
                case 1:
                    // 保证只会产生65~90之间的整数
                    data = randomData.nextInt(26) + 65;
                    sb.append((char) data);
                    break;
                case 2:
                    // 保证只会产生97~122之间的整数
                    data = randomData.nextInt(26) + 97;
                    sb.append((char) data);
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

}
