package com.yaya.operationLog.dao;

import com.yaya.orderapi.operationLogDTO.OperationLogDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 操作日志mapper类
 */
@Mapper
public interface OperationLogMapper {

    /**
     * 新增操作日志
     * @param operationLogDTO
     */
    void addOperationLog(OperationLogDTO operationLogDTO);

    /**
     * 获取日志记录列表
     * @param operationLogDTO
     * @return
     */
    List<OperationLogDTO> getOperationLogs(OperationLogDTO operationLogDTO);
}
