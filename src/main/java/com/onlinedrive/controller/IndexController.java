package com.onlinedrive.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.onlinedrive.domain.File;
import com.onlinedrive.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequestMapping("/index")
@RestController
public class IndexController {

    @Autowired
    FileService fileService;

    @GetMapping("/getAllFile/{currentPage}/{pageSize}")
    public IPage<File> getAllFile(@PathVariable("currentPage") Long currentPage, @PathVariable("pageSize") Long pageSize) {
        return fileService.getAllFile(currentPage, pageSize);
    }

    @GetMapping("/getFileById/{fileId}")
    public File getFileById(@PathVariable("fileId") int fileId){
        return fileService.getFileById(fileId);
    }

    @GetMapping("/searchFile/{currentPage}/{pageSize}/{type}/{content}")
    public IPage<File> searchFile(@PathVariable("currentPage") Long currentPage, @PathVariable("pageSize") Long pageSize,@PathVariable("type") String type, @PathVariable("content") String content) {
        return fileService.searchFile(currentPage, pageSize, type, content);
    }

    //文件上传
    @PostMapping("/toUpload")
    public boolean toUpload(@RequestBody MultipartFile file,@RequestParam("contributor") String contributor, @RequestParam("remarks") String remarks, HttpServletRequest request) throws IOException {
       try{
           String dir = request.getServletContext().getRealPath("/fileLoad/");  //存放目录
           System.out.println(dir);
           java.io.File path = new java.io.File(dir);  //确认路径存在
           if (!path.exists()) {
               path.mkdirs();
           }
           String fileName = file.getOriginalFilename();
           file.transferTo(new java.io.File(dir + fileService.repeatedNaming(fileName,fileService.getFileId())));
           fileService.insertFile(fileName,contributor,file.getSize(),remarks);
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }

    //文件下载
    @GetMapping("/toDownload/{fileName}/{fileId}")
    public ResponseEntity<byte[]> toDownload(@PathVariable("fileName")String fileName, @PathVariable("fileId")int fileId, HttpServletRequest request) {
        return fileService.downloadFile(request, fileName, fileId);
    }
}
