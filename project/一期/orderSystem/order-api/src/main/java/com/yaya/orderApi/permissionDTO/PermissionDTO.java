package com.yaya.orderApi.permissionDTO;


import com.yaya.orderApi.permissionModel.Permission;
import lombok.Data;

import java.util.Date;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 权限实体扩展类
 */
@Data
public class PermissionDTO extends Permission {
    //用户类型字符串
    private String userTypeStr;

    //用户类型
    private String userType;

    private Date lastUpdateDate;
    //用户权限中间表ID
    private String userPermissionId;
    //用户权限状态表
    private String userPermissionStatus;
}
