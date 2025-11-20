package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用文件夹实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_folder")
public class AppFolder extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 文件夹名称
     */
    private String name;
    
    /**
     * 文件夹图标（可选）
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
     * 排序序号
     */
    private Integer sortOrder;
}

