package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 回收站实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recycle_bin")
public class RecycleBin extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 删除类型：APP/FOLDER/WIDGET
     */
    private String itemType;
    
    /**
     * 删除的项目ID
     */
    private Long itemId;
    
    /**
     * 删除的项目数据（JSON格式，用于恢复）
     */
    private String itemData;
    
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
    
    /**
     * 自动清理时间（删除时间+保留天数）
     */
    private LocalDateTime autoCleanTime;
}

