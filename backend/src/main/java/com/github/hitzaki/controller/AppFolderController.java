package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.entity.AppFolder;
import com.github.hitzaki.service.AppFolderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用文件夹控制器
 */
@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
@SaCheckLogin
public class AppFolderController {
    
    private final AppFolderService folderService;
    
    /**
     * 创建文件夹
     */
    @PostMapping("/create")
    public Result<AppFolder> createFolder(@RequestBody CreateFolderRequest request) {
        try {
            Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            AppFolder folder = folderService.createFolder(
                    userId,
                    request.getName(),
                    request.getX(),
                    request.getY()
            );
            return Result.success(folder);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户文件夹列表
     */
    @PostMapping("/list")
    public Result<List<AppFolder>> getFolderList() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<AppFolder> folders = folderService.getUserFolders(userId);
        return Result.success(folders);
    }
    
    /**
     * 获取文件夹内的应用
     */
    @PostMapping("/apps")
    public Result<List<App>> getFolderApps(@RequestBody FolderIdRequest request) {
        List<App> apps = folderService.getFolderApps(request.getFolderId());
        return Result.success(apps);
    }
    
    /**
     * 添加应用到文件夹
     */
    @PostMapping("/addApp")
    public Result<Void> addAppToFolder(@RequestBody AddAppToFolderRequest request) {
        folderService.addAppToFolder(request.getAppId(), request.getFolderId());
        return Result.success(null);
    }
    
    /**
     * 从文件夹移除应用
     */
    @PostMapping("/removeApp")
    public Result<Void> removeAppFromFolder(@RequestBody AppIdRequest request) {
        folderService.removeAppFromFolder(request.getAppId());
        return Result.success(null);
    }
    
    /**
     * 更新文件夹位置
     */
    @PostMapping("/updatePosition")
    public Result<Void> updatePosition(@RequestBody UpdateFolderPositionRequest request) {
        folderService.updateFolderPosition(request.getFolderId(), request.getX(), request.getY());
        return Result.success(null);
    }
    
    /**
     * 删除文件夹
     */
    @PostMapping("/delete")
    public Result<Void> deleteFolder(@RequestBody FolderIdRequest request) {
        try {
            Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            folderService.deleteFolder(userId, request.getFolderId());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新文件夹
     */
    @PostMapping("/update")
    public Result<Void> updateFolder(@RequestBody AppFolder folder) {
        folderService.updateFolder(folder);
        return Result.success(null);
    }
    
    @lombok.Data
    public static class CreateFolderRequest {
        private String name;
        private Integer x;
        private Integer y;
    }
    
    @lombok.Data
    public static class FolderIdRequest {
        private Long folderId;
    }
    
    @lombok.Data
    public static class AddAppToFolderRequest {
        private Long appId;
        private Long folderId;
    }
    
    @lombok.Data
    public static class AppIdRequest {
        private Long appId;
    }
    
    @lombok.Data
    public static class UpdateFolderPositionRequest {
        private Long folderId;
        private Integer x;
        private Integer y;
    }
}

