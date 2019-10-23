package com.yaya.orderApi.merchantDTO;

import com.yaya.orderApi.merchantModel.Merchant;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description
 */
@Data
@RedisHash("merchantDTO")
public class MerchantDTO extends Merchant {

    private String createDateTimeStr;

    private String lastUpdateDateTimeStr;

}
