package com.yaya.product.template;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/20
 * @description 商家产品信息导出
 */
@Data
public class ProductExportResult{

    @Excel(name = "商家名称",orderNum = "0",width = 20)
    private String merchantName;

    @Excel(name = "产品名称",orderNum = "1",width = 20)
    private String productName;

    @Excel(name = "产品描述",orderNum = "2",width = 20)
    private String productDescription;

    @Excel(name = "产品价格",orderNum = "3",type = 10)
    private BigDecimal productPrice;

    @Excel(name = "订取次数",orderNum = "4")
    private Integer productOrderCount;

    @Excel(name = "创建时间",orderNum = "5")
    private String createTime;


}
