package com.yaya.merchant.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.MerchantEnableConstant;
import com.yaya.common.constant.OperationTypeConstant;
import com.yaya.common.constant.UserTypeConstant;
import com.yaya.common.util.DateUtils;
import com.yaya.common.util.SaltUtil;
import com.yaya.common.util.UUIDUtil;
import com.yaya.merchant.dao.MerchantMapperExt;
import com.yaya.merchant.service.MerchantService;
import com.yaya.merchant.template.MerchantExportResult;
import com.yaya.orderApi.merchantDTO.MerchantDTO;
import com.yaya.orderApi.merchantModel.Merchant;
import com.yaya.orderApi.merchantModel.MerchantExample;
import com.yaya.orderApi.operationLogInterface.OperationLogHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家Service实现类
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapperExt merchantMapperExt;

    @Autowired
    private OperationLogHandler operationLogHandler;

    @Override
    public Optional<MerchantDTO> loginByMerchantName(MerchantDTO merchantDTO) {
        merchantDTO = merchantMapperExt.loginByMerchantName(merchantDTO);
        Optional<MerchantDTO> merchantDTOOptional = Optional.ofNullable(merchantDTO);
        if (merchantDTOOptional.isPresent()) {
            return merchantDTOOptional;
        }
        return Optional.empty();
    }

    @Override
    public MerchantDTO merchantRegister(MerchantDTO merchantDTO) {
        // 生成uuid主键
        String uuid = UUIDUtil.getUUID();
        merchantDTO.setMerchantId(uuid);

        // 生成盐值
        String salt = SaltUtil.createRandomCharData(6);
        merchantDTO.setSalt(salt);

        //加密密码
        String encodePassword = DigestUtils.md5DigestAsHex((merchantDTO.getMerchantPassword() + salt).getBytes());
        merchantDTO.setMerchantPassword(encodePassword);

        merchantDTO.setUserType(UserTypeConstant.MERCHANT_TYPE);
        merchantDTO.setMerchantEnable(MerchantEnableConstant.MERCHANT_ENABLE);

        Date date = new Date();
        merchantDTO.setCreateTime(date);
        merchantDTO.setLastUpdateTime(date);

        merchantMapperExt.insertSelective(merchantDTO);

        // 发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.REGISTER_MERCHANT, uuid, uuid, "商家注册");
        return merchantDTO;
    }

    @Override
    public Optional<MerchantDTO> getMerchantById(String merchantId) {
        Merchant merchant = merchantMapperExt.selectByPrimaryKey(merchantId);

        Optional<Merchant> merchantOptional = Optional.ofNullable(merchant);
        if (merchantOptional.isPresent()) {
            MerchantDTO merchantDTO = new MerchantDTO();
            BeanUtils.copyProperties(merchant,merchantDTO);

            merchantDTO.setCreateDateTimeStr(DateUtils.dateToStr(merchantDTO.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            merchantDTO.setLastUpdateDateTimeStr(DateUtils.dateToStr(merchantDTO.getLastUpdateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));

            return Optional.ofNullable(merchantDTO);
        }
        return Optional.empty();
    }

    @Override
    public PageInfo<MerchantDTO> getPageMerchants(MerchantDTO merchantDTO, Integer pageNum, Integer pageSize) {
        List<MerchantDTO> merchantDTOList = new ArrayList<>();

        PageHelper.startPage(pageNum, pageSize);

        MerchantExample merchantExample = new MerchantExample();
        MerchantExample.Criteria criteria = merchantExample.createCriteria();
        criteria.andMerchantEnableEqualTo(merchantDTO.getMerchantEnable());
        criteria.andMerchantAddressLike(merchantDTO.getMerchantAddress());
        criteria.andMerchantNameLike(merchantDTO.getMerchantName());

        List<Merchant> merchantList = merchantMapperExt.selectByExample(merchantExample);
        merchantList.stream().forEach(merchant -> {
            MerchantDTO mer = new MerchantDTO();
            BeanUtils.copyProperties(merchant,mer);

            mer.setCreateDateTimeStr(DateUtils.dateToStr(merchant.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            mer.setLastUpdateDateTimeStr(DateUtils.dateToStr(merchant.getLastUpdateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            merchantDTOList.add(mer);
        });
        return new PageInfo<>(merchantDTOList);
    }

    @Override
    public List<MerchantExportResult> getExportMerchants(MerchantDTO merchantDTO, int pageSize) {
        List<MerchantExportResult> merchantExportResultList = new ArrayList<>();
        PageInfo<MerchantDTO> merchantDTOPageInfo = null;
        for (int i = 0; i == 0 || i <= (merchantDTOPageInfo.getTotal() - 1) / pageSize; i++) {
            merchantDTOPageInfo = getPageMerchants(merchantDTO, i + 1, pageSize);
            merchantDTOPageInfo.getList().forEach(merchant -> {
                MerchantExportResult merchantExportResult = new MerchantExportResult();
                merchantExportResult.setMerchantName(merchant.getMerchantName());
                merchantExportResult.setMerchantTelPhone(merchant.getMerchantTelPhone());
                merchantExportResult.setMerchantMobilephone(merchant.getMerchantMobilephone());
                merchantExportResult.setMerchantBusinessLicense(merchant.getMerchantBusinessLicense());
                merchantExportResult.setMerchantAddress(merchant.getMerchantAddress());
                merchantExportResult.setCreateTimeStr(DateUtils.dateToStr(merchant.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
                merchantExportResultList.add(merchantExportResult);
            });
        }
        return merchantExportResultList;
    }

    @Override
    public void updateMerchant(MerchantDTO merchantDTO) {
        merchantMapperExt.updateByPrimaryKeySelective(merchantDTO);

        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.REGISTER_MERCHANT, merchantDTO.getMerchantId(), merchantDTO.getMerchantId(), "更新商家信息");
    }

    @Override
    public void updateMerchantEnable(MerchantDTO merchantDTO) {
        merchantMapperExt.updateByPrimaryKeySelective(merchantDTO);

        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.UPDATE_MERCHANT_ENABLE, merchantDTO.getMerchantId(), merchantDTO.getMerchantId(), "更新商家是否可用状态:" + merchantDTO.getMerchantEnable());
    }
}
