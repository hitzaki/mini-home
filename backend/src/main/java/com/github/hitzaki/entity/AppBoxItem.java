package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用盒子中的项目
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_box_item")
public class AppBoxItem extends BaseEntity {
    
    /**
     * 应用盒子ID
     */
    private Long boxId;
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
}

