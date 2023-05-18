package com.example.javasec.controller;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class FileUpload {
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer();
        //获取原文件名称和后缀
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀名
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        String path =  System.getProperty("user.dir") + "\\src\\main\\resources\\uploads\\";
        try {
            File file1 = new File(path,originalFilename+ext);
            file.transferTo(file1);
            buffer.append(path);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("upload:"+originalFilename+ext+"success!");
        return "success";
    }

}
