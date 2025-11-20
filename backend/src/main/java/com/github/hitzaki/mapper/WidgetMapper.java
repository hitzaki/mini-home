package com.github.hitzaki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hitzaki.entity.Widget;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WidgetMapper extends BaseMapper<Widget> {
}

