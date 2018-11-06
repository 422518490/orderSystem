package com.yaya.permission.service.impl;

import com.yaya.orderApi.permissionDTO.UserPermissionDTO;
import com.yaya.orderApi.permissionModel.UserPermission;
import com.yaya.permission.dao.UserPermissionMapperExt;
import com.yaya.permission.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/8
 * @description 用户权限中间关系Service实现类
 */
@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    @Autowired
    private UserPermissionMapperExt userPermissionMapperExt;

    @Override
    public void updateTypeStatus(UserPermissionDTO userPermissionDTO) {
        userPermissionMapperExt.updateTypeStatus(userPermissionDTO);
    }

    @Override
    public UserPermissionDTO getUserPermissionByTypeAndPermission(UserPermissionDTO userPermissionDTO) {
        return userPermissionMapperExt.getUserPermissionByTypeAndPermission(userPermissionDTO);
    }

    @Override
    public void addUserPermission(UserPermission userPermission) {
        userPermissionMapperExt.insert(userPermission);
    }

    @Override
    public void delUserPermission(UserPermissionDTO userPermissionDTO) {
        userPermissionMapperExt.delUserPermission(userPermissionDTO);
    }
}
