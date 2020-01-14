package com.yaya.permission.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.PermissionUpdateNoticeConstant;
import com.yaya.common.util.RedisUtil;
import com.yaya.common.util.SetSelfRedis;
import com.yaya.common.util.UUIDUtil;
import com.yaya.orderApi.permissionDTO.PermissionDTO;
import com.yaya.orderApi.permissionDTO.UserPermissionDTO;
import com.yaya.orderApi.permissionModel.Permission;
import com.yaya.orderApi.permissionModel.UserPermission;
import com.yaya.permission.constant.UserPermissionStatus;
import com.yaya.permission.dao.PermissionMapperExt;
import com.yaya.permission.service.PermissionService;
import com.yaya.permission.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 权限Service实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapperExt permissionMapperExt;
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addPermission(PermissionDTO permissionDTO) {
        //判断该方法是否已经存在
        PermissionDTO per = getPermissionByMethod(permissionDTO.getMethodName());
        Date date = new Date();
        //获取uuid主键
        String uuid = UUIDUtil.getUUID();
        if(per == null){
            Permission permission = new Permission();
            permission.setMethodName(permissionDTO.getMethodName());;
            permission.setPermissionId(uuid);
            permission.setPermissionName(permissionDTO.getPermissionName());
            permission.setCreateDate(date);
            permissionMapperExt.insert(permission);
        }else {
            uuid = per.getPermissionId();
        }

        //新增UserPermission相关数据
        UserPermission userPermission = new UserPermission();
        userPermission.setPermissionId(uuid);
        userPermission.setUserPermissionStatus(UserPermissionStatus.ENABLE);
        userPermission.setCreateDate(date);
        userPermission.setLastUpdateDate(date);
        for (String userType : permissionDTO.getUserTypeStr().split(",")){
            //如果存在该方法
            if(per != null){
                //判断是否已经存在该用户类型的权限关系，如果存在，
                //判断用户权限中间表的USER_PERMISSION_STATUS状态，
                //如果失效，则更新为有效
                UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
                userPermissionDTO.setUserType(userType);
                userPermissionDTO.setPermissionId(userPermission.getPermissionId());
                userPermissionDTO = userPermissionService.getUserPermissionByTypeAndPermission(userPermissionDTO);
                if(userPermissionDTO != null){
                    if(UserPermissionStatus.DISABLE.equals(userPermissionDTO.getUserPermissionStatus())){
                        //更新方法对应的用户类型为有效
                        userPermissionDTO.setUserPermissionStatus(UserPermissionStatus.ENABLE);
                        userPermissionService.updateTypeStatus(userPermissionDTO);
                    }
                }else {
                    addUserPermission(userType,userPermission);
                }
            }else{
                addUserPermission(userType,userPermission);
            }
        }
        //新增方法权限后，在redis里面新增表示权限变化的标识用于验证
        redisUtil.set(PermissionUpdateNoticeConstant.PERMISSION_UPDATE_NOTICE, "01");
    }


    @Override
    public PageInfo<PermissionDTO> getPermissions(PermissionDTO permissionDTO,Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            pageNum = 1;
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<PermissionDTO> permissionDTOList = permissionMapperExt.getPermissions(permissionDTO);
        return new PageInfo<PermissionDTO>(permissionDTOList);
    }

    @Override
    public PermissionDTO getPermissionByMethod(String methodName) {
        return permissionMapperExt.getPermissionByMethod(methodName);
    }

    @Override
    public List<String> getMethodNameByType(String userType) {
        return permissionMapperExt.getMethodNameByType(userType);
    }

    private void addUserPermission(String userType,UserPermission userPermission){
        userPermission.setUserPermissionId(UUIDUtil.getUUID());
        userPermission.setUserType(userType);
        userPermissionService.addUserPermission(userPermission);
    }
}
