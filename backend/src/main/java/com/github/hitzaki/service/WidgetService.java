package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.hitzaki.entity.Widget;
import com.github.hitzaki.mapper.WidgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小组件服务
 */
@Service
@RequiredArgsConstructor
public class WidgetService {
    
    private final WidgetMapper widgetMapper;
    
    /**
     * 添加小组件
     */
    public Widget addWidget(Long userId, String type, String config, Integer x, Integer y, Integer width, Integer height) {
        Widget widget = new Widget();
        widget.setUserId(userId);
        widget.setType(type);
        widget.setConfig(config);
        widget.setPositionX(x);
        widget.setPositionY(y);
        widget.setWidth(width != null ? width : 200);
        widget.setHeight(height != null ? height : 150);
        widgetMapper.insert(widget);
        return widget;
    }
    
    /**
     * 获取用户的桌面小组件
     */
    public List<Widget> getUserWidgets(Long userId) {
        LambdaQueryWrapper<Widget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Widget::getUserId, userId);
        wrapper.orderByAsc(Widget::getSortOrder);
        return widgetMapper.selectList(wrapper);
    }
    
    /**
     * 更新小组件位置
     */
    public void updateWidgetPosition(Long widgetId, Integer x, Integer y) {
        LambdaUpdateWrapper<Widget> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Widget::getId, widgetId);
        wrapper.set(Widget::getPositionX, x);
        wrapper.set(Widget::getPositionY, y);
        widgetMapper.update(null, wrapper);
    }
    
    /**
     * 更新小组件大小
     */
    public void updateWidgetSize(Long widgetId, Integer width, Integer height) {
        LambdaUpdateWrapper<Widget> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Widget::getId, widgetId);
        wrapper.set(Widget::getWidth, width);
        wrapper.set(Widget::getHeight, height);
        widgetMapper.update(null, wrapper);
    }
    
    /**
     * 更新小组件配置
     */
    public void updateWidgetConfig(Long widgetId, String config) {
        LambdaUpdateWrapper<Widget> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Widget::getId, widgetId);
        wrapper.set(Widget::getConfig, config);
        widgetMapper.update(null, wrapper);
    }
    
    /**
     * 删除小组件
     */
    public void deleteWidget(Long widgetId) {
        widgetMapper.deleteById(widgetId);
    }
    
    /**
     * 更新小组件
     */
    public void updateWidget(Widget widget) {
        widgetMapper.updateById(widget);
    }
}

