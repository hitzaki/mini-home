package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app")
public class App extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用图标URL
     */
    private String icon;
    
    /**
     * 应用类型：WEB/NATIVE
     */
    private String type;
    
    /**
     * 应用URL（Web应用使用）
     */
    private String url;
    
    /**
     * 打开方式：EXTERNAL/INTERNAL
     */
    private String openType;
    
    /**
     * 是否在桌面显示
     */
    private Integer showOnDesktop;
    
    /**
     * 桌面位置X坐标
     */
    private Integer positionX;
    
    /**
     * 桌面位置Y坐标
     */
    private Integer positionY;
    
    /**
     * 所属文件夹ID（如果为空则在桌面根目录）
     */
    private Long folderId;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 是否为新应用（跨端同步标记）
     */
    private Integer isNew;
}

