package com.yaya.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/4
 * @description 文件上传处理工具类
 */
@Component
@PropertySource(ignoreResourceNotFound=true,value= "classpath:config/application-common.properties")
public class UploadFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaiDuMapUtil.class);

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 上传多个文件，返回文件名称和服务器存储路径列表
     * @param files
     * @return
     */
    public Map<String ,String> uploadFiles(Map<String,MultipartFile> files) throws IOException {
        Map<String, String> uploadResult = new HashMap<String, String>();
        File file = new File(uploadPath);
        //判断文件夹是否存在
        if(!file.exists()){
            file.mkdir();
        }
        //准备循环文件
        Iterator<Map.Entry<String, MultipartFile>> fileIterator = files.entrySet().iterator();
        while (fileIterator.hasNext()){
            MultipartFile multipartFile = fileIterator.next().getValue();
            //如果文件有大小且文件名字不为空
            if(multipartFile.getSize() != 0 && !"".equals(multipartFile.getName())){
                uploadResult.put(multipartFile.getOriginalFilename(), uploadFile(multipartFile));
            }
        }
        return uploadResult;
    }

    /**
     * 上传单个文件，并返回其在服务器中的存储路径
     * @param multipartFile
     * @return
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String filePath = initFilePath(multipartFile.getOriginalFilename());
        write(multipartFile.getInputStream(), new FileOutputStream(filePath));
        return filePath;
    }

    private void write(InputStream inputStream , OutputStream outputStream){
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }catch (Exception e){
            LOGGER.error("上传文件流出错：" + e);
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("关闭文件流出错：" + e);
            }
        }
    }

    /**
     * 返回文件存储路径，为防止重名文件被覆盖，在文件名称中增加了随机数
     * @param name
     * @return
     */
    private String initFilePath(String name){
        String dir = getFileDir(name) + "";
        File file = new File(uploadPath + dir);
        if(!file.exists()){
            file.mkdir();
        }
        Long time = new Date().getTime();
        Double randomTime = Math.random() * time;
        return (file.getPath() +System.getProperty("file.separator") + time + randomTime.longValue() + "_" + name).replaceAll(" ", "-");
    }

    /**
     * 计算文件夹路径
     * @param name
     * @return
     */
    private int getFileDir(String name){
        return name.hashCode() & 0xf;
    }

}
