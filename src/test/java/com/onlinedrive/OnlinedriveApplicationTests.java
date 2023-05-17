package com.onlinedrive;

import com.onlinedrive.mapper.FileMapper;
import com.onlinedrive.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class OnlinedriveApplicationTests {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    FileService fileService;
    @Test
    void contextLoads() {

    }

}
