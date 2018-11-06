package com.yaya.permission.dao;

import com.yaya.orderApi.permissionDTO.PermissionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 权限mapper接口扩展类
 */
@Mapper
public interface PermissionMapperExt extends PermissionMapper {

    /**
     * 获取权限方法
     * @param permissionDTO
     * @return
     */
    public List<PermissionDTO> getPermissions(PermissionDTO permissionDTO);

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
