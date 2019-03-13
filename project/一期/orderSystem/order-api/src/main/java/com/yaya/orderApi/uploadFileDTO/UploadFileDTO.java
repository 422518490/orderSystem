package com.yaya.orderapi.uploadFileDTO;


import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/6
 * @description 上传文件的返回类
 */
@Data
public class UploadFileDTO {

    private String fileName;

    private String filePath;
}
