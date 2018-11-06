package com.yaya.operationLog.service;

import com.github.pagehelper.PageInfo;
import com.yaya.orderApi.operationLogDTO.OperationLogDTO;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 操作日志service接口类
 */
public interface OperationLogService {

    /**
     * 新增操作日志
     * @param operationLogDTO
     */
    void addOperationLog(OperationLogDTO operationLogDTO);


    /**
     * 分页获取日志记录
     * @param operationLogDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<OperationLogDTO> getPageProducts(OperationLogDTO operationLogDTO, Integer pageNum, Integer pageSize);

}
