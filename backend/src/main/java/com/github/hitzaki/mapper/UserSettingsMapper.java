package com.github.hitzaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hitzaki.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}

