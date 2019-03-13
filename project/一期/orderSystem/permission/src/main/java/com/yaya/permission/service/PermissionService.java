package com.yaya.permission.service;


import com.github.pagehelper.PageInfo;
import com.yaya.orderapi.permissionDTO.PermissionDTO;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 权限Service接口类
 */
public interface PermissionService {

    /**
     * 添加权限
     * @param permissionDTO
     * @return
     */
    public void addPermission(PermissionDTO permissionDTO);

    /**
     * 获取权限方法
     * @param permissionDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<PermissionDTO> getPermissions(PermissionDTO permissionDTO, Integer pageNum, Integer pageSize);


    /**
     * 根据方法名称获取权限
     * @param methodName
     * @return
     */
    public PermissionDTO getPermissionByMethod(String methodName);

    /**
     * 根据用户类型获取方法列表
     * @param userType
     * @return
     */
    public List<String> getMethodNameByType(String userType);

}
