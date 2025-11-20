package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.service.WebsiteInfoService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 网站信息控制器
 */
@RestController
@RequestMapping("/website")
@RequiredArgsConstructor
@SaCheckLogin
public class WebsiteInfoController {
    
    private final WebsiteInfoService websiteInfoService;
    
    /**
     * 获取网站信息（标题和图标）
     */
    @PostMapping("/getInfo")
    public Result<WebsiteInfo> getWebsiteInfo(@RequestBody WebsiteUrlRequest request) {
        String title = websiteInfoService.getWebsiteTitle(request.getUrl());
        String icon = websiteInfoService.getWebsiteIcon(request.getUrl());
        
        WebsiteInfo info = new WebsiteInfo();
        info.setTitle(title);
        info.setIcon(icon);
        
        return Result.success(info);
    }
    
    @lombok.Data
    public static class WebsiteUrlRequest {
        private String url;
    }
    
    @lombok.Data
    public static class WebsiteInfo {
        private String title;
        private String icon;
    }
}

