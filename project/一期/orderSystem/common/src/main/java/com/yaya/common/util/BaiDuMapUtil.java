package com.yaya.common.util;

import com.alibaba.fastjson.JSONObject;
import com.yaya.common.response.BaiDuAddressResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/6
 * @description 百度地图工具类
 */
@Component
@PropertySource(ignoreResourceNotFound=true,value= "classpath:config/application-common.properties")
public class BaiDuMapUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaiDuMapUtil.class);
    @Value("${baidu.address}")
    private String baiduAddress;

    /**
     * 详细地址转换为经纬度信息
     * @param address
     * @return
     */
    public BaiDuAddressResult getLocation(String address) {
        BaiDuAddressResult baiDuAddressResult = new BaiDuAddressResult();
        try {
            StringBuffer stringBuffer = new StringBuffer();
            String urlParam = baiduAddress;
            urlParam = urlParam + "&address=" + address +"&output=json";

            URL url = new URL(urlParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(500000);
            conn.setConnectTimeout(500000);
            conn.setUseCaches(false);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String tmp = stringBuffer.toString();
            if(tmp != null && !"".equals(tmp)){
                JSONObject baiDuLngLat= JSONObject.parseObject(tmp);
                LOGGER.info("百度转换坐标:"+baiDuLngLat);
                baiDuAddressResult.setStatus(Integer.parseInt(baiDuLngLat.getString("status")));
                baiDuLngLat = JSONObject.parseObject(baiDuLngLat.getString("result"));
                baiDuAddressResult.setConfidence(Integer.parseInt(baiDuLngLat.getString("confidence")));
                baiDuAddressResult.setLevel(baiDuLngLat.getString("level"));
                baiDuAddressResult.setPrecise(Integer.parseInt(baiDuLngLat.getString("precise")));
                baiDuAddressResult.setLocation(JSONObject.parseObject(baiDuLngLat.get("location").toString()));
                LOGGER.info("地址信息:"+address+",经纬度:"+baiDuAddressResult.getLocation().getString("lat")+","+baiDuAddressResult.getLocation().getString("lng"));
            }else{
                LOGGER.info("百度地图没有正确解析，链接地址："+urlParam);
            }
            reader.close();
            conn.disconnect();
         } catch (Exception e) {
            baiDuAddressResult.setStatus(-1);
            LOGGER.error(" 经纬度转换出错:"+e.getMessage());
            e.printStackTrace();
        }
        return baiDuAddressResult;
    }
}
