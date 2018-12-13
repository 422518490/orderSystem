package com.yaya.permission.controller;

import com.github.pagehelper.PageInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yaya.common.response.*;
import com.yaya.common.util.ValidatorUtil;
import com.yaya.orderApi.permissionDTO.PermissionDTO;
import com.yaya.orderApi.permissionInterface.PermissionControllerInterface;
import com.yaya.permission.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 权限controller类
 */
@RestController
public class PermissionController implements PermissionControllerInterface{

    private static Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    /**
     * 添加权限
     * @param permissionDTO
     * @return
     */
    //@RequestMapping(value = "/addPermission",method = RequestMethod.POST)
    @Override
    public BaseResponse addPermission(@RequestBody PermissionDTO permissionDTO){
        BaseResponse baseResponse = new BaseResponse();
        int validateReturnCode = 0;
        try {
            Map<String,String> errorMap = new HashMap<String, String>();
            //验证权限名称
            validateReturnCode = ValidatorUtil.validParameterAlert(permissionDTO.getPermissionName(),"权限名称",true,1,50,4,errorMap);
            if(validateReturnCode != 0){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg(errorMap.get("权限名称"));
                return baseResponse;
            }
            //验证方法名称
            validateReturnCode = ValidatorUtil.validParameterAlert(permissionDTO.getMethodName(),"方法名称",true,1,45,4,errorMap);
            if(validateReturnCode != 0){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg(errorMap.get("方法名称"));
                return baseResponse;
            }
            //验证方法权限对应的用户类型
            if(ValidatorUtil.isEmpty(permissionDTO.getUserTypeStr())){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("用户类型不能为空");
                return baseResponse;
            }
            permissionService.addPermission(permissionDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("新增权限方法成功");
        }catch (Exception e){
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
        }
        return baseResponse;
    }

    /**
     * 获取权限方法
     * @param userType
     * @return
     */
    //@RequestMapping(value = "/getPermissions")
    @Override
    public PageResponse<PermissionDTO> getPermissions(@RequestParam(value = "userType",required = false)String userType,
                                                               @RequestParam(value = "permissionName",required = false)String permissionName,
                                                               @RequestParam(value = "pageNum",required = false)Integer pageNum,
                                                               @RequestParam(value = "pageSize",required = false)Integer pageSize){
        PageResponse<PermissionDTO> pageResponse = new PageResponse<PermissionDTO>();
        try {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setUserType(userType);
            permissionDTO.setPermissionName(permissionName);
            PageInfo<PermissionDTO> permissionDTOList = permissionService.getPermissions(permissionDTO,pageNum,pageSize);
            pageResponse.setData(permissionDTOList);
            pageResponse.setCode(ResponseCode.SUCCESS);
            pageResponse.setMsg("获取方法权限列表成功");
        }catch (Exception e){
            pageResponse.setCode(ResponseCode.SERVER_ERROR);
            pageResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
        }
        return pageResponse;
    }

    /**
     * 根据用户类型获取方法列表
     * @param userType
     * @return
     */
    //@RequestMapping(value = "/getMethodNameByType")
    @Override
    @HystrixCommand
    public MultiDataResponse<String> getMethodNameByType(@RequestParam(value = "userType")String userType){
        MultiDataResponse<String> multiDataResponse = new MultiDataResponse<>();
        try {
            //
            TimeUnit.MINUTES.sleep(5);
            List<String> methodNameList = permissionService.getMethodNameByType(userType);
            multiDataResponse.setData(methodNameList);
            multiDataResponse.setCode(ResponseCode.SUCCESS);
            multiDataResponse.setMsg("获取方法列表成功");
        }catch (Exception e){
            multiDataResponse.setCode(ResponseCode.SERVER_ERROR);
            multiDataResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
        }
        return multiDataResponse;
    }

    @GetMapping(value = "/getMethodNameByTypeHystrix")
    public MultiDataResponse<String> getMethodNameByTypeHystrix(@RequestParam(value = "userType")String userType){
        MultiDataResponse<String> multiDataResponse = new MultiDataResponse<>();
        try {
            List<String> methodNameList = new ArrayList<>();
            multiDataResponse.setData(methodNameList);
            multiDataResponse.setCode(ResponseCode.SUCCESS);
            multiDataResponse.setMsg("获取方法列表成功");
        }catch (Exception e){
            multiDataResponse.setCode(ResponseCode.SERVER_ERROR);
            multiDataResponse.setMsg("服务器错误");
            logger.error("SERVER_ERROR: " + e);
        }
        return multiDataResponse;
    }



}
