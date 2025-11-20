package com.github.hitzaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hitzaki.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMapper extends BaseMapper<App> {
}

