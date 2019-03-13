package com.yaya.permission.dao;

import com.yaya.orderapi.permissionDTO.UserPermissionDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 用户权限中间关系mapper扩展类
 */
@Mapper
public interface UserPermissionMapperExt extends UserPermissionMapper {

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
     * 删除用户类型对应的方法权限
     * @param userPermissionDTO
     */
    public void delUserPermission(UserPermissionDTO userPermissionDTO);

}
