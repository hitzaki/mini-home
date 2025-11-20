package com.github.hitzaki.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.hitzaki.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密后）
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 设备类型：PC/MOBILE
     */
    private String deviceType;
    
    /**
     * 设备标识
     */
    private String deviceId;
}

