package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.RecycleBin;
import com.github.hitzaki.service.RecycleBinService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收站控制器
 */
@RestController
@RequestMapping("/recycleBin")
@RequiredArgsConstructor
@SaCheckLogin
public class RecycleBinController {
    
    private final RecycleBinService recycleBinService;
    
    /**
     * 获取回收站列表
     */
    @PostMapping("/list")
    public Result<List<RecycleBin>> getList() {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        List<RecycleBin> list = recycleBinService.getRecycleBinList(userId);
        return Result.success(list);
    }
    
    /**
     * 恢复项目
     */
    @PostMapping("/restore")
    public Result<Void> restore(@RequestBody RecycleBinIdRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        recycleBinService.restore(userId, request.getRecycleBinId());
        return Result.success(null);
    }
    
    /**
     * 彻底删除
     */
    @PostMapping("/deletePermanently")
    public Result<Void> deletePermanently(@RequestBody RecycleBinIdRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        recycleBinService.deletePermanently(userId, request.getRecycleBinId());
        return Result.success(null);
    }
    
    @lombok.Data
    public static class RecycleBinIdRequest {
        private Long recycleBinId;
    }
}

