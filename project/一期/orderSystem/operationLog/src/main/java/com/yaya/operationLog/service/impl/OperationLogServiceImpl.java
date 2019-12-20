package com.yaya.operationLog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.common.util.DateUtils;
import com.yaya.operationLog.dao.OperationLogMapper;
import com.yaya.operationLog.service.OperationLogService;
import com.yaya.orderApi.operationLogDTO.OperationLogDTO;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 操作日志service实现类
 */
@Service
@RabbitListener(queues = "operationLogQueue")
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    @RabbitHandler
    public void addOperationLog(OperationLogDTO operationLogDTO) {
        operationLogMapper.addOperationLog(operationLogDTO);
    }

    @Override
    public PageInfo<OperationLogDTO> getPageProducts(OperationLogDTO operationLogDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLogDTO> operationLogDTOList = operationLogMapper.getOperationLogs(operationLogDTO);
        operationLogDTOList.forEach(operationLog ->{
            operationLog.setCreateTimeStr(DateUtils.dateToStr(operationLog.getCreateTime(),DateUtils.DEFAULT_DATETIME_FORMAT));
        });
        return new PageInfo<>(operationLogDTOList);
    }
}
