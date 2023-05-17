package com.onlinedrive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_file")
public class File {
    @TableId(type= IdType.ASSIGN_ID)
    int fileId;
    String fileName;
    String contributor;
    String fileType;
    String fileSize;
    int downloadTimes;
    String uploadTime;
    boolean deleted;
    String remarks;
}
