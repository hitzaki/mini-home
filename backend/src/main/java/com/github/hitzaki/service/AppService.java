package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.entity.RecycleBin;
import com.github.hitzaki.exception.BusinessException;
import com.github.hitzaki.mapper.AppMapper;
import com.github.hitzaki.mapper.RecycleBinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用服务
 */
@Service
@RequiredArgsConstructor
public class AppService {
    
    private final AppMapper appMapper;
    private final RecycleBinMapper recycleBinMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 添加应用
     */
    public App addApp(Long userId, String name, String icon, String type, String url, String openType) {
        App app = new App();
        app.setUserId(userId);
        app.setName(name);
        app.setIcon(icon);
        app.setType(type);
        app.setUrl(url);
        app.setOpenType(openType);
        app.setShowOnDesktop(1);
        app.setIsNew(0);
        // 设置默认位置（随机位置，避免重叠）
        app.setPositionX((int)(Math.random() * 500) + 50);
        app.setPositionY((int)(Math.random() * 300) + 50);
        appMapper.insert(app);
        return app;
    }
    
    /**
     * 获取用户的所有应用
     */
    public List<App> getUserApps(Long userId) {
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(App::getUserId, userId);
        wrapper.orderByAsc(App::getSortOrder);
        return appMapper.selectList(wrapper);
    }
    
    /**
     * 获取桌面应用
     */
    public List<App> getDesktopApps(Long userId) {
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(App::getUserId, userId);
        wrapper.eq(App::getShowOnDesktop, 1);
        wrapper.isNull(App::getFolderId);
        wrapper.orderByAsc(App::getSortOrder);
        return appMapper.selectList(wrapper);
    }
    
    /**
     * 更新应用位置
     */
    public void updateAppPosition(Long appId, Integer x, Integer y) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getPositionX, x);
        wrapper.set(App::getPositionY, y);
        appMapper.update(null, wrapper);
    }
    
    /**
     * 从桌面移除应用
     */
    public void removeFromDesktop(Long appId) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getShowOnDesktop, 0);
        appMapper.update(null, wrapper);
    }
    
    /**
     * 添加到桌面
     */
    public void addToDesktop(Long appId, Integer x, Integer y) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getShowOnDesktop, 1);
        wrapper.set(App::getPositionX, x);
        wrapper.set(App::getPositionY, y);
        appMapper.update(null, wrapper);
    }
    
    /**
     * 卸载应用（移至回收站）
     */
    @Transactional
    public void uninstallApp(Long userId, Long appId) {
        App app = appMapper.selectById(appId);
        if (app == null) {
            throw new BusinessException(404, "应用不存在");
        }
        
        // 验证权限：只能卸载自己的应用
        if (!app.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限操作此应用");
        }
        
        // 保存到回收站
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setUserId(userId);
        recycleBin.setItemType("APP");
        recycleBin.setItemId(appId);
        try {
            recycleBin.setItemData(objectMapper.writeValueAsString(app));
        } catch (Exception e) {
            throw new BusinessException(500, "序列化应用数据失败", e);
        }
        recycleBin.setDeleteTime(LocalDateTime.now());
        recycleBin.setAutoCleanTime(LocalDateTime.now().plusDays(30));
        recycleBinMapper.insert(recycleBin);
        
        // 删除应用
        appMapper.deleteById(appId);
    }
    
    /**
     * 搜索应用
     */
    public List<App> searchApps(Long userId, String keyword) {
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(App::getUserId, userId);
        wrapper.like(App::getName, keyword);
        wrapper.orderByAsc(App::getSortOrder);
        return appMapper.selectList(wrapper);
    }
    
    /**
     * 更新应用
     */
    public void updateApp(App app) {
        appMapper.updateById(app);
    }
    
    /**
     * 标记新应用为已读
     */
    public void markAsRead(Long appId) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getIsNew, 0);
        appMapper.update(null, wrapper);
    }
    
    /**
     * 根据ID获取应用
     */
    public App getAppById(Long appId) {
        return appMapper.selectById(appId);
    }
}

