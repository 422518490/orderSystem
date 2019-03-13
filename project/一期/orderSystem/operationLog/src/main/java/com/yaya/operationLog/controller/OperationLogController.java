package com.yaya.operationLog.controller;

import com.github.pagehelper.PageInfo;
import com.yaya.common.response.PageResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.operationLog.service.OperationLogService;
import com.yaya.orderapi.operationLogDTO.OperationLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 操作日志controller类
 */
@RestController
@Slf4j
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping(value = "/operationLog/getPageOperationLog")
    public PageResponse<OperationLogDTO> getPageOperationLog(OperationLogDTO operationLogDTO,
                                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        PageResponse<OperationLogDTO> pageResponse = new PageResponse<>();
        try {
            PageInfo<OperationLogDTO> operationLogDTOPageInfo = operationLogService.getPageProducts(operationLogDTO,pageNum,pageSize);
            pageResponse.setData(operationLogDTOPageInfo);
            pageResponse.setCode(ResponseCode.SUCCESS);
            pageResponse.setMsg("获取日志信息列表成功");
        }catch (Exception e){
            log.error("分页获取日志信息错误:" + e);
            pageResponse.setCode(ResponseCode.SERVER_ERROR);
            pageResponse.setMsg("服务器错误");
            e.printStackTrace();
        }
        return pageResponse;
    }

}
