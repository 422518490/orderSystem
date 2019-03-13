package com.yaya.orderapi.operationLogDTO;

import com.yaya.orderapi.operationLogModel.OperationLog;
import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/24
 * @description 操作日志实体扩展类
 */
@Data
public class OperationLogDTO extends OperationLog {

    /**
     * 操作日志类型说明
     */
    private String operationTypeValue;

    private String createTimeStr;

}
