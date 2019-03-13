package com.yaya.permission.controller;

import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.common.util.ValidatorUtil;
import com.yaya.orderapi.permissionDTO.UserPermissionDTO;
import com.yaya.permission.constant.UserPermissionStatus;
import com.yaya.permission.service.UserPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/8
 * @description 用户权限中间关系controller类
 */
@RestController
public class UserPermissionController {

    private  static  Logger logger = LoggerFactory.getLogger(UserPermissionController.class);

    @Autowired
    private UserPermissionService userPermissionService;

    /**
     * 更新类型对应的权限有效性
     * @param userPermissionDTO
     */
    @RequestMapping(value = "/updateTypeStatus",method = RequestMethod.POST)
    public BaseResponse updateTypeStatus(@RequestBody UserPermissionDTO userPermissionDTO){
        BaseResponse baseResponse = new BaseResponse();
        try {
            if(ValidatorUtil.isEmpty(userPermissionDTO.getUserType())){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("用户类型不能为空");
                return baseResponse;
            }
            String userPermissionStatus = userPermissionDTO.getUserPermissionStatus();
            if(ValidatorUtil.isEmpty(userPermissionStatus)){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("用户类型有效性不能为空");
                return baseResponse;
            }
            if(!UserPermissionStatus.DISABLE.equals(userPermissionStatus)
                    && !UserPermissionStatus.ENABLE.equals(userPermissionStatus)){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("用户类型有效性只能为00或01");
                return baseResponse;
            }
            String userPermissionIds = userPermissionDTO.getUserPermissionIds();
            if(!ValidatorUtil.isEmpty(userPermissionIds)){
                List<String> userPermissionIdList = new ArrayList<String>();
                for(String userPermissionId : userPermissionIds.split(",")){
                    userPermissionIdList.add(userPermissionId);
                }
                userPermissionDTO.setUserPermissionIdList(userPermissionIdList);
            }
            userPermissionService.updateTypeStatus(userPermissionDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("更新用户类型权限的有效性成功");
        }catch (Exception e){
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
            e.printStackTrace();
        }
        return baseResponse;
    }

    /**
     * 删除用户类型对应的方法权限
     * @param userPermissionIds
     * @return
     */
    @RequestMapping(value = "/delUserPermission",method = RequestMethod.DELETE)
    public BaseResponse delUserPermission(@RequestParam(value = "userPermissionIds")String userPermissionIds){
        BaseResponse baseResponse = new BaseResponse();
        try{
            List<String> userPermissionIdList = new ArrayList<String>();
            if("".equals(userPermissionIds.split(",")[0].trim())){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("用户权限中间表ID格式不正确");
                return baseResponse;
            }
            for(String userPermissionId : userPermissionIds.split(",")){
                userPermissionIdList.add(userPermissionId);
            }
            UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
            userPermissionDTO.setUserPermissionIdList(userPermissionIdList);
            userPermissionService.delUserPermission(userPermissionDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("删除用户类型对应的方法成功");
        }catch (Exception e){
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
            e.printStackTrace();
        }
        return baseResponse;
    }

}
