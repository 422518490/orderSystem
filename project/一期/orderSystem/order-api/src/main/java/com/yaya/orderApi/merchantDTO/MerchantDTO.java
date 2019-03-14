package com.yaya.orderApi.merchantDTO;

import com.yaya.orderApi.merchantModel.Merchant;
import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description
 */
@Data
public class MerchantDTO extends Merchant {

    private String createDateTimeStr;

    private String lastUpdateDateTimeStr;

}
