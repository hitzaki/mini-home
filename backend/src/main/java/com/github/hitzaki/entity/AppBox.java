package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用盒子实体（类似iTop Easy Desktop的悬浮面板）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_box")
public class AppBox extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 盒子名称
     */
    private String name;
    
    /**
     * 盒子图标
     */
    private String icon;
    
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
     * 是否展开
     */
    private Integer isExpanded;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
}

