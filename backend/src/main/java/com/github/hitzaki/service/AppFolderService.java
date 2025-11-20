package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.entity.AppFolder;
import com.github.hitzaki.entity.RecycleBin;
import com.github.hitzaki.mapper.AppFolderMapper;
import com.github.hitzaki.mapper.AppMapper;
import com.github.hitzaki.mapper.RecycleBinMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用文件夹服务
 */
@Service
@RequiredArgsConstructor
public class AppFolderService {
    
    private final AppFolderMapper folderMapper;
    private final AppMapper appMapper;
    private final RecycleBinMapper recycleBinMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建文件夹
     */
    public AppFolder createFolder(Long userId, String name, Integer x, Integer y) {
        AppFolder folder = new AppFolder();
        folder.setUserId(userId);
        folder.setName(name);
        folder.setPositionX(x);
        folder.setPositionY(y);
        folderMapper.insert(folder);
        return folder;
    }
    
    /**
     * 获取用户的文件夹列表
     */
    public List<AppFolder> getUserFolders(Long userId) {
        LambdaQueryWrapper<AppFolder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppFolder::getUserId, userId);
        wrapper.orderByAsc(AppFolder::getSortOrder);
        return folderMapper.selectList(wrapper);
    }
    
    /**
     * 获取文件夹内的应用
     */
    public List<App> getFolderApps(Long folderId) {
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(App::getFolderId, folderId);
        wrapper.orderByAsc(App::getSortOrder);
        return appMapper.selectList(wrapper);
    }
    
    /**
     * 添加应用到文件夹
     */
    public void addAppToFolder(Long appId, Long folderId) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getFolderId, folderId);
        wrapper.set(App::getShowOnDesktop, 0); // 从桌面移除
        appMapper.update(null, wrapper);
    }
    
    /**
     * 从文件夹移除应用
     */
    public void removeAppFromFolder(Long appId) {
        LambdaUpdateWrapper<App> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(App::getId, appId);
        wrapper.set(App::getFolderId, null);
        appMapper.update(null, wrapper);
    }
    
    /**
     * 更新文件夹位置
     */
    public void updateFolderPosition(Long folderId, Integer x, Integer y) {
        LambdaUpdateWrapper<AppFolder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AppFolder::getId, folderId);
        wrapper.set(AppFolder::getPositionX, x);
        wrapper.set(AppFolder::getPositionY, y);
        folderMapper.update(null, wrapper);
    }
    
    /**
     * 删除文件夹（移至回收站）
     */
    @Transactional
    public void deleteFolder(Long userId, Long folderId) {
        AppFolder folder = folderMapper.selectById(folderId);
        if (folder == null) {
            throw new RuntimeException("文件夹不存在");
        }
        
        // 将文件夹内的应用移出
        List<App> apps = getFolderApps(folderId);
        for (App app : apps) {
            removeAppFromFolder(app.getId());
        }
        
        // 保存到回收站
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setUserId(userId);
        recycleBin.setItemType("FOLDER");
        recycleBin.setItemId(folderId);
        try {
            recycleBin.setItemData(objectMapper.writeValueAsString(folder));
        } catch (Exception e) {
            throw new RuntimeException("序列化文件夹数据失败", e);
        }
        recycleBin.setDeleteTime(LocalDateTime.now());
        recycleBin.setAutoCleanTime(LocalDateTime.now().plusDays(30));
        recycleBinMapper.insert(recycleBin);
        
        // 删除文件夹
        folderMapper.deleteById(folderId);
    }
    
    /**
     * 更新文件夹
     */
    public void updateFolder(AppFolder folder) {
        folderMapper.updateById(folder);
    }
}

