package com.yaya.common.response;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 分页对象返回
 */
@Data
public class PageResponse<T> extends BaseResponse{
    /**
     * 对象信息
     */
    private PageInfo<T> data;
}
