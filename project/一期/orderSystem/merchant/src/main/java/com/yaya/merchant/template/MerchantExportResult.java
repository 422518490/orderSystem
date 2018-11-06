package com.yaya.merchant.template;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/22
 * @description 商家信息导出
 */
@Data
public class MerchantExportResult {

    @Excel(name = "商家名称",orderNum = "0",width = 20)
    private String merchantName;

    @Excel(name = "商家座机号码",orderNum = "1")
    private String merchantTelPhone;

    @Excel(name = "商家手机号码",orderNum = "2")
    private String merchantMobilephone;

    @Excel(name = "营业执照编号",orderNum = "3",width = 15)
    private String merchantBusinessLicense;

    @Excel(name = "商家地址",orderNum = "4",width = 30)
    private String merchantAddress;

    @Excel(name = "创建时间",orderNum = "5")
    private String createTimeStr;

}
