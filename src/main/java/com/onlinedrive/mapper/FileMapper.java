package com.onlinedrive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinedrive.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
}
