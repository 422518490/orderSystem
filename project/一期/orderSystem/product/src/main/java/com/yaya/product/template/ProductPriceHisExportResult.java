package com.yaya.product.template;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/20
 * @description 商家产品历史价格信息导出
 */
@Data
public class ProductPriceHisExportResult {

    @Excel(name = "商家名称",orderNum = "0",width = 20)
    private String merchantName;

    @Excel(name = "产品名称",orderNum = "1",width = 20)
    private String productName;

    @Excel(name = "产品价格",orderNum = "2",type = 10)
    private BigDecimal productPrice;

    @Excel(name = "创建时间",orderNum = "3")
    private String createTime;


}
