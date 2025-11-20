package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.hitzaki.entity.UserSettings;
import com.github.hitzaki.mapper.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户设置服务
 */
@Service
@RequiredArgsConstructor
public class UserSettingsService {
    
    private final UserSettingsMapper userSettingsMapper;
    
    /**
     * 获取或创建用户设置
     */
    public UserSettings getOrCreateSettings(Long userId, String deviceType) {
        LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSettings::getUserId, userId);
        wrapper.eq(UserSettings::getDeviceType, deviceType);
        UserSettings settings = userSettingsMapper.selectOne(wrapper);
        
        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setDeviceType(deviceType);
            settings.setTheme("default");
            settings.setDefaultOpenType("INTERNAL");
            settings.setRecycleBinAutoCleanDays(30);
            userSettingsMapper.insert(settings);
        }
        
        return settings;
    }
    
    /**
     * 更新用户设置
     */
    public void updateSettings(UserSettings settings) {
        // 先查询现有设置
        LambdaQueryWrapper<UserSettings> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSettings::getUserId, settings.getUserId());
        queryWrapper.eq(UserSettings::getDeviceType, settings.getDeviceType());
        UserSettings existing = userSettingsMapper.selectOne(queryWrapper);
        
        if (existing == null) {
            // 如果不存在，创建新设置
            userSettingsMapper.insert(settings);
        } else {
            // 如果存在，使用LambdaUpdateWrapper确保所有字段都能更新（包括null值）
            LambdaUpdateWrapper<UserSettings> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserSettings::getId, existing.getId());
            updateWrapper.set(UserSettings::getTheme, settings.getTheme());
            updateWrapper.set(UserSettings::getWallpaper, settings.getWallpaper());
            updateWrapper.set(UserSettings::getDefaultOpenType, settings.getDefaultOpenType());
            updateWrapper.set(UserSettings::getRecycleBinAutoCleanDays, settings.getRecycleBinAutoCleanDays());
            updateWrapper.set(UserSettings::getOtherSettings, settings.getOtherSettings());
            userSettingsMapper.update(null, updateWrapper);
        }
    }
}

