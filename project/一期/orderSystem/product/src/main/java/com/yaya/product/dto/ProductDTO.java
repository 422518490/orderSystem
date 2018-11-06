package com.yaya.product.dto;

import com.yaya.product.model.Product;
import lombok.Data;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/18
 * @description 产品实体扩展类
 */
@Data
public class ProductDTO extends Product {

    /**
     * 产品ID集合
     */
    private List<String> productIdList;

    /**
     * 产品ID字符串集合
     */
    private String productIdStr;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 创建时间的字符串
     */
    private String createTimeStr;

    /**
     * 最后更新时间的字符串
     */
    private String lastUpdateTimeStr;

    /**
     * 登陆用户的id
     */
    private String loginUserId;

}
