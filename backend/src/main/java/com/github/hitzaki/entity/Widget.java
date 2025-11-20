package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 桌面小组件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("widget")
public class Widget extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 组件类型：CLOCK/CALENDAR/WEATHER/SEARCH/PERFORMANCE
     */
    private String type;
    
    /**
     * 组件配置（JSON格式）
     */
    private String config;
    
    /**
     * 桌面位置X坐标
     */
    private Integer positionX;
    
    /**
     * 桌面位置Y坐标
     */
    private Integer positionY;
    
    /**
     * 宽度
     */
    private Integer width;
    
    /**
     * 高度
     */
    private Integer height;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
}

