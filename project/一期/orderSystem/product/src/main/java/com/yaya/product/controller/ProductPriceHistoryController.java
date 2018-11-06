package com.yaya.product.controller;

import com.github.pagehelper.PageInfo;
import com.yaya.common.response.PageResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.common.util.ExcelUtils;
import com.yaya.product.dto.ProductPriceHistoryDTO;
import com.yaya.product.service.ProductPriceHistoryService;
import com.yaya.product.template.ProductPriceHisExportResult;
import com.yaya.security.access.AccessRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description 产品历史价格controller类
 */
@RestController
@Slf4j
public class ProductPriceHistoryController {

    @Autowired
    private ProductPriceHistoryService productPriceHistoryService;

    /**
     * 分页获取商家的产品价格历史信息
     * @param productPriceHistoryDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/getPageProductPriceHis")
    @AccessRequired
    public PageResponse<ProductPriceHistoryDTO> getPageProductPriceHis(ProductPriceHistoryDTO productPriceHistoryDTO,
                                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResponse<ProductPriceHistoryDTO> pageResponse = new PageResponse<>();
        try {
            if(StringUtils.isEmpty(productPriceHistoryDTO.getMerchantId())){
                pageResponse.setCode(ResponseCode.PARAMETER_ERROR);
                pageResponse.setMsg("商家ID不能为空");
                return pageResponse;
            }
            PageInfo<ProductPriceHistoryDTO> productPriceHistoryDTOPageInfo = productPriceHistoryService.getPageProductPriceHis(productPriceHistoryDTO,pageNum,pageSize);
            pageResponse.setData(productPriceHistoryDTOPageInfo);
            pageResponse.setCode(ResponseCode.SUCCESS);
            pageResponse.setMsg("获取商家产品价格历史信息列表成功");
        }catch (Exception e){
            log.error("获取商家产品价格历史信息错误:" + e);
            pageResponse.setCode(ResponseCode.SERVER_ERROR);
            pageResponse.setMsg("服务器错误");
            e.printStackTrace();
        }
        return pageResponse;
    }

    /**
     * 导出商家产品历史价格
     * @param productPriceHistoryDTO
     * @param httpServletResponse
     */
    @PostMapping(value = "/product/priceHisExport")
    @AccessRequired
    public void exportProduct(@RequestBody ProductPriceHistoryDTO productPriceHistoryDTO, HttpServletResponse httpServletResponse) {
        try {
            ExcelUtils.exportExcel(productPriceHistoryService.getExportProductPriceHis(productPriceHistoryDTO,500),
                    "商家产品价格历史记录导出", "商家产品价格历史记录导出", ProductPriceHisExportResult.class,"商家产品价格历史记录导出.xlsx",httpServletResponse);
        }catch (Exception e){
            log.error("导出商家产品信息错误:" + e);
            e.printStackTrace();
        }
    }
}
