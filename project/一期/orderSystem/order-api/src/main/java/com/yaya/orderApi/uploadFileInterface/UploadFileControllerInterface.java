package com.yaya.orderapi.uploadFileInterface;

import com.yaya.common.response.MultiDataResponse;
import com.yaya.orderapi.uploadFileDTO.UploadFileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/7
 * @description 上传文件的api
 */
public interface UploadFileControllerInterface {

    /**
     * 上传文件
     * @param request
     * @return
     */
    @PostMapping(value = "/uploadFiles")
    MultiDataResponse<UploadFileDTO> uploadFiles(HttpServletRequest request);

    /**
     * 下载文件
     * @param request
     * @param filePathName
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/downloadFile")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request,
                                               @RequestParam(value = "filePathName")String filePathName) throws Exception;

}
