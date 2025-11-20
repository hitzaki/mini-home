package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.UserSettings;
import com.github.hitzaki.service.UserSettingsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@SaCheckLogin
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;
    
    /**
     * 获取用户设置
     */
    @PostMapping("/get")
    public Result<UserSettings> getSettings(@RequestBody DeviceTypeRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        UserSettings settings = userSettingsService.getOrCreateSettings(userId, request.getDeviceType());
        return Result.success(settings);
    }
    
    /**
     * 更新用户设置
     */
    @PostMapping("/update")
    public Result<Void> updateSettings(@RequestBody UserSettings settings) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        settings.setUserId(userId);
        userSettingsService.updateSettings(settings);
        return Result.success(null);
    }
    
    @lombok.Data
    public static class DeviceTypeRequest {
        private String deviceType;
    }
}

