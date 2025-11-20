package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.Widget;
import com.github.hitzaki.service.WidgetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小组件控制器
 */
@RestController
@RequestMapping("/widget")
@RequiredArgsConstructor
@SaCheckLogin
public class WidgetController {
    
    private final WidgetService widgetService;
    
    /**
     * 添加小组件
     */
    @PostMapping("/add")
    public Result<Widget> addWidget(@RequestBody AddWidgetRequest request) {
        try {
            Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            Widget widget = widgetService.addWidget(
                    userId,
                    request.getType(),
                    request.getConfig(),
                    request.getX(),
                    request.getY(),
                    request.getWidth(),
                    request.getHeight()
            );
            return Result.success(widget);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户小组件列表
     */
    @PostMapping("/list")
    public Result<List<Widget>> getWidgetList() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<Widget> widgets = widgetService.getUserWidgets(userId);
        return Result.success(widgets);
    }
    
    /**
     * 更新小组件位置
     */
    @PostMapping("/updatePosition")
    public Result<Void> updatePosition(@RequestBody UpdateWidgetPositionRequest request) {
        widgetService.updateWidgetPosition(request.getWidgetId(), request.getX(), request.getY());
        return Result.success(null);
    }
    
    /**
     * 更新小组件大小
     */
    @PostMapping("/updateSize")
    public Result<Void> updateSize(@RequestBody UpdateWidgetSizeRequest request) {
        widgetService.updateWidgetSize(request.getWidgetId(), request.getWidth(), request.getHeight());
        return Result.success(null);
    }
    
    /**
     * 更新小组件配置
     */
    @PostMapping("/updateConfig")
    public Result<Void> updateConfig(@RequestBody UpdateWidgetConfigRequest request) {
        widgetService.updateWidgetConfig(request.getWidgetId(), request.getConfig());
        return Result.success(null);
    }
    
    /**
     * 删除小组件
     */
    @PostMapping("/delete")
    public Result<Void> deleteWidget(@RequestBody WidgetIdRequest request) {
        widgetService.deleteWidget(request.getWidgetId());
        return Result.success(null);
    }
    
    /**
     * 更新小组件
     */
    @PostMapping("/update")
    public Result<Void> updateWidget(@RequestBody Widget widget) {
        widgetService.updateWidget(widget);
        return Result.success(null);
    }
    
    @lombok.Data
    public static class AddWidgetRequest {
        private String type;
        private String config;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
    
    @lombok.Data
    public static class WidgetIdRequest {
        private Long widgetId;
    }
    
    @lombok.Data
    public static class UpdateWidgetPositionRequest {
        private Long widgetId;
        private Integer x;
        private Integer y;
    }
    
    @lombok.Data
    public static class UpdateWidgetSizeRequest {
        private Long widgetId;
        private Integer width;
        private Integer height;
    }
    
    @lombok.Data
    public static class UpdateWidgetConfigRequest {
        private Long widgetId;
        private String config;
    }
}

