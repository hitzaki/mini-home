package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.exception.BusinessException;
import com.github.hitzaki.service.AppService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用控制器
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@SaCheckLogin
public class AppController {
    
    private final AppService appService;
    
    /**
     * 添加应用
     */
    @PostMapping("/add")
    public Result<App> addApp(@RequestBody AddAppRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        App app = appService.addApp(
                userId,
                request.getName(),
                request.getIcon(),
                request.getType(),
                request.getUrl(),
                request.getOpenType()
        );
        return Result.success(app);
    }
    
    /**
     * 获取用户所有应用
     */
    @PostMapping("/list")
    public Result<List<App>> getAppList() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<App> apps = appService.getUserApps(userId);
        return Result.success(apps);
    }
    
    /**
     * 获取桌面应用
     */
    @PostMapping("/desktop")
    public Result<List<App>> getDesktopApps() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<App> apps = appService.getDesktopApps(userId);
        return Result.success(apps);
    }
    
    /**
     * 更新应用位置
     */
    @PostMapping("/updatePosition")
    public Result<Void> updatePosition(@RequestBody UpdatePositionRequest request) {
        appService.updateAppPosition(request.getAppId(), request.getX(), request.getY());
        return Result.success(null);
    }
    
    /**
     * 从桌面移除
     */
    @PostMapping("/removeFromDesktop")
    public Result<Void> removeFromDesktop(@RequestBody AppIdRequest request) {
        appService.removeFromDesktop(request.getAppId());
        return Result.success(null);
    }
    
    /**
     * 添加到桌面
     */
    @PostMapping("/addToDesktop")
    public Result<Void> addToDesktop(@RequestBody AddToDesktopRequest request) {
        appService.addToDesktop(request.getAppId(), request.getX(), request.getY());
        return Result.success(null);
    }
    
    /**
     * 卸载应用
     */
    @PostMapping("/uninstall")
    public Result<Void> uninstall(@RequestBody AppIdRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        appService.uninstallApp(userId, request.getAppId());
        return Result.success(null);
    }
    
    /**
     * 搜索应用
     */
    @PostMapping("/search")
    public Result<List<App>> search(@RequestBody SearchRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<App> apps = appService.searchApps(userId, request.getKeyword());
        return Result.success(apps);
    }
    
    /**
     * 更新应用
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody App app) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        // 确保只能更新自己的应用
        App existingApp = appService.getAppById(app.getId());
        if (existingApp == null || !existingApp.getUserId().equals(userId)) {
            throw new BusinessException(403, "应用不存在或无权限");
        }
        app.setUserId(userId); // 确保userId正确
        appService.updateApp(app);
        return Result.success(null);
    }
    
    /**
     * 标记为已读
     */
    @PostMapping("/markAsRead")
    public Result<Void> markAsRead(@RequestBody AppIdRequest request) {
        appService.markAsRead(request.getAppId());
        return Result.success(null);
    }
    
    // 内部类
    @lombok.Data
    public static class AddAppRequest {
        private String name;
        private String icon;
        private String type;
        private String url;
        private String openType;
    }
    
    @lombok.Data
    public static class UpdatePositionRequest {
        private Long appId;
        private Integer x;
        private Integer y;
    }
    
    @lombok.Data
    public static class AppIdRequest {
        private Long appId;
    }
    
    @lombok.Data
    public static class AddToDesktopRequest {
        private Long appId;
        private Integer x;
        private Integer y;
    }
    
    @lombok.Data
    public static class SearchRequest {
        private String keyword;
    }
}

