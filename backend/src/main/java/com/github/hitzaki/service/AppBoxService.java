package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.hitzaki.entity.App;
import com.github.hitzaki.entity.AppBox;
import com.github.hitzaki.entity.AppBoxItem;
import com.github.hitzaki.mapper.AppBoxItemMapper;
import com.github.hitzaki.mapper.AppBoxMapper;
import com.github.hitzaki.mapper.AppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用盒子服务
 */
@Service
@RequiredArgsConstructor
public class AppBoxService {
    
    private final AppBoxMapper boxMapper;
    private final AppBoxItemMapper boxItemMapper;
    private final AppMapper appMapper;
    
    /**
     * 创建应用盒子
     */
    public AppBox createBox(Long userId, String name, Integer x, Integer y, Integer width, Integer height) {
        AppBox box = new AppBox();
        box.setUserId(userId);
        box.setName(name);
        box.setPositionX(x);
        box.setPositionY(y);
        box.setWidth(width != null ? width : 300);
        box.setHeight(height != null ? height : 400);
        box.setIsExpanded(1);
        boxMapper.insert(box);
        return box;
    }
    
    /**
     * 获取用户的应用盒子列表
     */
    public List<AppBox> getUserBoxes(Long userId) {
        LambdaQueryWrapper<AppBox> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppBox::getUserId, userId);
        wrapper.orderByAsc(AppBox::getSortOrder);
        return boxMapper.selectList(wrapper);
    }
    
    /**
     * 获取盒子内的应用
     */
    public List<App> getBoxApps(Long boxId) {
        LambdaQueryWrapper<AppBoxItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppBoxItem::getBoxId, boxId);
        wrapper.orderByAsc(AppBoxItem::getSortOrder);
        List<AppBoxItem> items = boxItemMapper.selectList(wrapper);
        
        List<Long> appIds = items.stream()
                .map(AppBoxItem::getAppId)
                .collect(Collectors.toList());
        
        if (appIds.isEmpty()) {
            return List.of();
        }
        
        return appMapper.selectBatchIds(appIds);
    }
    
    /**
     * 添加应用到盒子
     */
    @Transactional
    public void addAppToBox(Long appId, Long boxId) {
        // 检查是否已存在
        LambdaQueryWrapper<AppBoxItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppBoxItem::getBoxId, boxId);
        wrapper.eq(AppBoxItem::getAppId, appId);
        if (boxItemMapper.selectCount(wrapper) > 0) {
            return; // 已存在，不重复添加
        }
        
        // 获取当前最大排序号
        LambdaQueryWrapper<AppBoxItem> maxWrapper = new LambdaQueryWrapper<>();
        maxWrapper.eq(AppBoxItem::getBoxId, boxId);
        maxWrapper.orderByDesc(AppBoxItem::getSortOrder);
        maxWrapper.last("LIMIT 1");
        AppBoxItem lastItem = boxItemMapper.selectOne(maxWrapper);
        int nextSort = lastItem != null ? lastItem.getSortOrder() + 1 : 0;
        
        AppBoxItem item = new AppBoxItem();
        item.setBoxId(boxId);
        item.setAppId(appId);
        item.setSortOrder(nextSort);
        boxItemMapper.insert(item);
    }
    
    /**
     * 从盒子移除应用
     */
    public void removeAppFromBox(Long appId, Long boxId) {
        LambdaQueryWrapper<AppBoxItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppBoxItem::getBoxId, boxId);
        wrapper.eq(AppBoxItem::getAppId, appId);
        boxItemMapper.delete(wrapper);
    }
    
    /**
     * 更新盒子位置
     */
    public void updateBoxPosition(Long boxId, Integer x, Integer y) {
        LambdaUpdateWrapper<AppBox> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AppBox::getId, boxId);
        wrapper.set(AppBox::getPositionX, x);
        wrapper.set(AppBox::getPositionY, y);
        boxMapper.update(null, wrapper);
    }
    
    /**
     * 更新盒子大小
     */
    public void updateBoxSize(Long boxId, Integer width, Integer height) {
        LambdaUpdateWrapper<AppBox> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AppBox::getId, boxId);
        wrapper.set(AppBox::getWidth, width);
        wrapper.set(AppBox::getHeight, height);
        boxMapper.update(null, wrapper);
    }
    
    /**
     * 切换盒子展开/收起状态
     */
    public void toggleBox(Long boxId) {
        AppBox box = boxMapper.selectById(boxId);
        if (box != null) {
            LambdaUpdateWrapper<AppBox> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AppBox::getId, boxId);
            wrapper.set(AppBox::getIsExpanded, box.getIsExpanded() == 1 ? 0 : 1);
            boxMapper.update(null, wrapper);
        }
    }
    
    /**
     * 删除盒子
     */
    @Transactional
    public void deleteBox(Long boxId) {
        // 删除盒子内的所有应用关联
        LambdaQueryWrapper<AppBoxItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppBoxItem::getBoxId, boxId);
        boxItemMapper.delete(wrapper);
        
        // 删除盒子
        boxMapper.deleteById(boxId);
    }
    
    /**
     * 更新盒子
     */
    public void updateBox(AppBox box) {
        boxMapper.updateById(box);
    }
}

