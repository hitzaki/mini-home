package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户设置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_settings")
public class UserSettings extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 设备类型：PC/MOBILE
     */
    private String deviceType;
    
    /**
     * 主题名称
     */
    private String theme;
    
    /**
     * 壁纸URL
     */
    private String wallpaper;
    
    /**
     * 默认应用打开方式：EXTERNAL/INTERNAL
     */
    private String defaultOpenType;
    
    /**
     * 回收站自动清理天数（0表示不自动清理）
     */
    private Integer recycleBinAutoCleanDays;
    
    /**
     * 其他设置（JSON格式）
     */
    private String otherSettings;
}

