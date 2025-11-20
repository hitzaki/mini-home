package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.entity.AppBox;
import com.github.hitzaki.service.AppBoxService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用盒子控制器
 */
@RestController
@RequestMapping("/box")
@RequiredArgsConstructor
@SaCheckLogin
public class AppBoxController {
    
    private final AppBoxService boxService;
    
    /**
     * 创建应用盒子
     */
    @PostMapping("/create")
    public Result<AppBox> createBox(@RequestBody CreateBoxRequest request) {
        try {
            Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            AppBox box = boxService.createBox(
                    userId,
                    request.getName(),
                    request.getX(),
                    request.getY(),
                    request.getWidth(),
                    request.getHeight()
            );
            return Result.success(box);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户盒子列表
     */
    @PostMapping("/list")
    public Result<List<AppBox>> getBoxList() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<AppBox> boxes = boxService.getUserBoxes(userId);
        return Result.success(boxes);
    }
    
    /**
     * 获取盒子内的应用
     */
    @PostMapping("/apps")
    public Result<List<App>> getBoxApps(@RequestBody BoxIdRequest request) {
        List<App> apps = boxService.getBoxApps(request.getBoxId());
        return Result.success(apps);
    }
    
    /**
     * 添加应用到盒子
     */
    @PostMapping("/addApp")
    public Result<Void> addAppToBox(@RequestBody AddAppToBoxRequest request) {
        boxService.addAppToBox(request.getAppId(), request.getBoxId());
        return Result.success(null);
    }
    
    /**
     * 从盒子移除应用
     */
    @PostMapping("/removeApp")
    public Result<Void> removeAppFromBox(@RequestBody RemoveAppFromBoxRequest request) {
        boxService.removeAppFromBox(request.getAppId(), request.getBoxId());
        return Result.success(null);
    }
    
    /**
     * 更新盒子位置
     */
    @PostMapping("/updatePosition")
    public Result<Void> updatePosition(@RequestBody UpdateBoxPositionRequest request) {
        boxService.updateBoxPosition(request.getBoxId(), request.getX(), request.getY());
        return Result.success(null);
    }
    
    /**
     * 更新盒子大小
     */
    @PostMapping("/updateSize")
    public Result<Void> updateSize(@RequestBody UpdateBoxSizeRequest request) {
        boxService.updateBoxSize(request.getBoxId(), request.getWidth(), request.getHeight());
        return Result.success(null);
    }
    
    /**
     * 切换盒子展开/收起
     */
    @PostMapping("/toggle")
    public Result<Void> toggleBox(@RequestBody BoxIdRequest request) {
        boxService.toggleBox(request.getBoxId());
        return Result.success(null);
    }
    
    /**
     * 删除盒子
     */
    @PostMapping("/delete")
    public Result<Void> deleteBox(@RequestBody BoxIdRequest request) {
        boxService.deleteBox(request.getBoxId());
        return Result.success(null);
    }
    
    /**
     * 更新盒子
     */
    @PostMapping("/update")
    public Result<Void> updateBox(@RequestBody AppBox box) {
        boxService.updateBox(box);
        return Result.success(null);
    }
    
    @lombok.Data
    public static class CreateBoxRequest {
        private String name;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }
    
    @lombok.Data
    public static class BoxIdRequest {
        private Long boxId;
    }
    
    @lombok.Data
    public static class AddAppToBoxRequest {
        private Long appId;
        private Long boxId;
    }
    
    @lombok.Data
    public static class RemoveAppFromBoxRequest {
        private Long appId;
        private Long boxId;
    }
    
    @lombok.Data
    public static class UpdateBoxPositionRequest {
        private Long boxId;
        private Integer x;
        private Integer y;
    }
    
    @lombok.Data
    public static class UpdateBoxSizeRequest {
        private Long boxId;
        private Integer width;
        private Integer height;
    }
}

