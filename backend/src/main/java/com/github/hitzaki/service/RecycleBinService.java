package com.github.hitzaki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.hitzaki.entity.RecycleBin;
import com.github.hitzaki.exception.BusinessException;
import com.github.hitzaki.mapper.RecycleBinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回收站服务
 */
@Service
@RequiredArgsConstructor
public class RecycleBinService {
    
    private final RecycleBinMapper recycleBinMapper;
    
    /**
     * 获取回收站列表
     */
    public List<RecycleBin> getRecycleBinList(Long userId) {
        LambdaQueryWrapper<RecycleBin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleBin::getUserId, userId);
        wrapper.orderByDesc(RecycleBin::getDeleteTime);
        return recycleBinMapper.selectList(wrapper);
    }
    
    /**
     * 恢复项目
     */
    @Transactional
    public void restore(Long userId, Long recycleBinId) {
        RecycleBin recycleBin = recycleBinMapper.selectById(recycleBinId);
        if (recycleBin == null || !recycleBin.getUserId().equals(userId)) {
            throw new BusinessException(404, "回收站项目不存在");
        }
        
        // 根据类型恢复（这里简化处理，实际需要根据itemType调用不同的Service）
        // 恢复逻辑在Controller中处理
        
        // 从回收站删除
        recycleBinMapper.deleteById(recycleBinId);
    }
    
    /**
     * 彻底删除
     */
    public void deletePermanently(Long userId, Long recycleBinId) {
        RecycleBin recycleBin = recycleBinMapper.selectById(recycleBinId);
        if (recycleBin == null || !recycleBin.getUserId().equals(userId)) {
            throw new BusinessException(404, "回收站项目不存在");
        }
        
        recycleBinMapper.deleteById(recycleBinId);
    }
    
    /**
     * 自动清理过期项目
     */
    public void autoClean() {
        LambdaQueryWrapper<RecycleBin> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(RecycleBin::getAutoCleanTime, LocalDateTime.now());
        recycleBinMapper.delete(wrapper);
    }
}

