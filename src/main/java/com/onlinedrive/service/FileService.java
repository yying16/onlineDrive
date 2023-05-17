package com.onlinedrive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinedrive.domain.File;
import com.onlinedrive.mapper.FileMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileService extends ServiceImpl<FileMapper, File> implements IService<File> {
    @Autowired
    FileMapper fileMapper;

    //返回所有文件
    public IPage<File> getAllFile(Long currentPage, Long pageSize) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        try {
            IPage page = new Page(currentPage, pageSize);
            fileMapper.selectPage(page, wrapper);
            return page;
        } catch (Exception e) {
            return new Page(currentPage, pageSize);
        }
    }

    //条件查询文件
    public IPage<File> searchFile(Long currentPage, Long pageSize, String type, String content) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        switch (type) {
            case "fileName":
                wrapper.like("file_name", "%" + content + "%");
                break;
            case "contributor":
                wrapper.like("contributor", "%" + content + "%");
                break;
            case "type":
                wrapper.like("file_type", "%" + content + "%");
                break;
        }
        try {
            IPage page = new Page(currentPage, pageSize);
            fileMapper.selectPage(page, wrapper);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new Page(currentPage, pageSize);
        }
    }

    //返回单个文件
    public File getFileById(int fileId) {
        return fileMapper.selectById(fileId);
    }

    //添加文件数据
    public boolean insertFile(String fileName, String contributor, long fileSize, String remarks) {
        try {
            File file = new File();
            file.setFileName(fileName);
            file.setContributor(contributor);
            file.setFileType(getSuffix(fileName));
            file.setFileSize(calculateFileSize(fileSize));
            file.setDownloadTimes(0);
            file.setDeleted(false);
            file.setUploadTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
            file.setRemarks(remarks);
            fileMapper.insert(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String calculateFileSize(long size) {
        double b = size;
        if (b < 1024) {
            return String.format("%.0fB", b);
        } else {
            double kb = b / 1024;
            if (kb < 1024) {
                return String.format("%.2fB", (b / 1024));
            } else {
                return String.format("%.2fB", (kb / 1024));
            }
        }
    }

    private String getSuffix(String fileName) {
        try {
            if (fileName.lastIndexOf(".") == -1) {//文件没有后缀名的情况
                return " ";
            }
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return " ";
        }
    }

    private String getPrefix(String fileName) {
        try {
            if (fileName.lastIndexOf(".") == -1) {//文件没有后缀名的情况
                return fileName;
            }
            return fileName.substring(0, fileName.length() - getSuffix(fileName).length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return fileName;
        }
    }

    public int getFileId(){
        return fileMapper.selectList(new QueryWrapper<>()).size() + 1;
    }

    public String repeatedNaming(String fileName,int fileId) { //解决重复命名问题
        String prefix = getPrefix(fileName);
        String suffix = getSuffix(fileName);
        return prefix + "_" + fileId + "." + suffix;
    }


    //文件下载
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, String fileName, int fileId) {
        try {
            fileName = repeatedNaming(fileName,fileId);
            String dir = request.getServletContext().getRealPath("/fileLoad");
            java.io.File file = new java.io.File(dir + java.io.File.separator + fileName);
            fileName = getFileName(request, fileName);  //对下载名进行转码
            HttpHeaders headers = new HttpHeaders();    //设置响应头
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); //以流的形式下载文件数据
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    private String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String[] IEBrowerKeyWords = {"MSIE", "Trident", "Edge"};    //IE不同版本User-Agent中出现的关键词
        String userAgent = request.getHeader("User-Agent");    //获得请求头代理信息
        for (String keyWord : IEBrowerKeyWords) {
            if (userAgent.contains(keyWord)) {
                //统一为UTF-8编码显示，并对转换的+进行更正
                return URLEncoder.encode(fileName, "UTF-8").replace("+", " ");
            }
        }
        return new String(fileName.getBytes("UTF-8"), "ISO-8859-1");//其他浏览器统一为ISO-8859-1编码显示
    }
}
