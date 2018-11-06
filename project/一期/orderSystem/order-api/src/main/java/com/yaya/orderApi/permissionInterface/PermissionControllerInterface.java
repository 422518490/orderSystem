package com.yaya.orderApi.permissionInterface;

import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.MultiDataResponse;
import com.yaya.common.response.PageResponse;
import com.yaya.orderApi.permissionDTO.PermissionDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/13
 * @description 用来实现feign的权限api
 */
//@RequestMapping(value = "/permission")
    //这个地方加上后会出错
public interface PermissionControllerInterface {

    /**
     * 添加权限
     * @param permissionDTO
     * @return
     */
    @PostMapping(value = "/permission/addPermission")
    BaseResponse addPermission(@RequestBody PermissionDTO permissionDTO);

    /**
     * 获取权限方法
     * @param userType
     * @return
     */
    @GetMapping(value = "/permission/getPermissions")
    PageResponse<PermissionDTO> getPermissions(@RequestParam(value = "userType",required = false)String userType,
                                                      @RequestParam(value = "permissionName",required = false)String permissionName,
                                                      @RequestParam(value = "pageNum",required = false)Integer pageNum,
                                                      @RequestParam(value = "pageSize",required = false)Integer pageSize);


    /**
     * 根据用户类型获取方法列表
     * @param userType
     * @return
     */
    @GetMapping(value = "/permission/getMethodNameByType")
    MultiDataResponse<String> getMethodNameByType(@RequestParam(value = "userType")String userType);

}
