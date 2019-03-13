package com.yaya.permission.service;


import com.yaya.orderapi.permissionDTO.UserPermissionDTO;
import com.yaya.orderapi.permissionModel.UserPermission;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/8
 * @description 用户权限中间关系service接口类
 */
public interface UserPermissionService {

    /**
     * 更新类型对应的权限有效性
     * @param userPermissionDTO
     */
    public void updateTypeStatus(UserPermissionDTO userPermissionDTO);

    /**
     *  根据用户类型和权限ID获取用户权限中间表信息
     * @param userPermissionDTO
     * @return
     */
    public UserPermissionDTO getUserPermissionByTypeAndPermission(UserPermissionDTO userPermissionDTO);

    /**
     * 新增用户类型权限中间信息
     * @param userPermission
     */
    public void addUserPermission(UserPermission userPermission);

    /**
     * 删除用户类型对应的方法权限
     * @param userPermissionDTO
     */
    public void delUserPermission(UserPermissionDTO userPermissionDTO);
}
