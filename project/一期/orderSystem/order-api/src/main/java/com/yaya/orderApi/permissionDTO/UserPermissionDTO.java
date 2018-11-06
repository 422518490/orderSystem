package com.yaya.orderApi.permissionDTO;


import com.yaya.orderApi.permissionModel.UserPermission;
import lombok.Data;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description
 */
@Data
public class UserPermissionDTO extends UserPermission {
    //用户权限中间表ID字符串集合
    private String userPermissionIds;

    private List<String> userPermissionIdList;

}
