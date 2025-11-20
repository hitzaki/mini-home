package com.github.hitzaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hitzaki.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

