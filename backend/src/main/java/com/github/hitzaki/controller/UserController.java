package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.entity.User;
import com.github.hitzaki.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getDeviceType(),
                request.getDeviceId()
        );
        return Result.success(user);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        
        LoginResponse response = new LoginResponse();
        response.setUser(user);
        response.setToken(token);
        
        return Result.success(response);
    }
    
    /**
     * 获取当前用户信息
     */
    @PostMapping("/info")
    @SaCheckLogin
    public Result<User> getInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        return Result.success(user);
    }
    
    // 内部类
    @lombok.Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String deviceType;
        private String deviceId;
    }
    
    @lombok.Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
    
    @lombok.Data
    public static class LoginResponse {
        private User user;
        private String token;
    }
}

