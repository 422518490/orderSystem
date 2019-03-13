package com.yaya.merchant.orderapi;

import com.yaya.orderapi.permissionInterface.PermissionControllerInterface;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/13
 * @description 公共接口继承类
 */
@FeignClient(value = "permission")
public interface PermissionInterface extends PermissionControllerInterface {
}
