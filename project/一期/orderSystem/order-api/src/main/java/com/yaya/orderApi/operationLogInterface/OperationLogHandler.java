package com.yaya.orderApi.operationLogInterface;

import com.yaya.common.util.UUIDUtil;
import com.yaya.orderApi.operationLogDTO.OperationLogDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/27
 * @description
 */
@Component
public class OperationLogHandler {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送日志给rabbit mq
     * @param operationType
     * @param fromId
     * @param toId
     * @param operationDesc
     */
    public void sendOperationLog(String operationType,String fromId,
                                 String toId,String operationDesc){
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        operationLogDTO.setOperationId(UUIDUtil.getUUID());
        operationLogDTO.setOperationType(operationType);
        operationLogDTO.setFromId(fromId);
        operationLogDTO.setToId(toId);
        operationLogDTO.setOperationDesc(operationDesc);
        amqpTemplate.convertAndSend("operationLogQueue",operationLogDTO);
    }

}
