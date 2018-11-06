package com.yaya.common.response;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

/**
 * Created by buchan on 2016/7/7 0007.
 */
public class BaiDuAddressResult {
    public BaiDuAddressResult(){
        this.status = 0;
    }

    //返回结果状态值， 成功返回0，其他值请查看下方返回码状态表
    private int status;

    //返回坐标
    private JSONObject location;
    //位置的附加信息，是否精确查找。
    // 1为精确查找，即准确打点；0为不精确，即模糊打点（模糊打点无法保证准确度，不建议使用）
    private int precise;
    //可信度，描述打点准确度，大于80表示误差小于100m。该字段仅作参考，返回结果准确度主要参考precise参数
    private int confidence;
    //能精确理解的地址类型，
    // 包含：UNKNOWN、国家、省、城市、区县、乡镇、村庄、道路、地产小区、商务大厦、
    // 政府机构、交叉路口、商圈、生活服务、休闲娱乐、餐饮、宾馆、购物、金融、教育、医疗 、
    // 工业园区 、旅游景点 、汽车服务、火车站、长途汽车站、桥 、停车场/停车区、港口/码头、
    // 收费区/收费站、飞机场 、机场 、收费处/收费站 、加油站、绿地、门址
    private String level;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public int getPrecise() {
        return precise;
    }

    public void setPrecise(int precise) {
        this.precise = precise;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
