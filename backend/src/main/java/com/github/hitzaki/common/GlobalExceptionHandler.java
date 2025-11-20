package com.github.hitzaki.common;

import cn.dev33.satoken.exception.NotLoginException;
import com.github.hitzaki.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 * 统一处理所有异常，返回前端可处理的响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     * 业务异常会被直接返回给前端，不记录错误日志（因为这是预期的业务逻辑）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.debug("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理SaToken登录异常（token无效、未登录等）
     * 返回401错误码，前端会自动跳转到登录页面
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<String> handleNotLoginException(NotLoginException e) {
        log.debug("用户未登录或token无效: {}", e.getMessage());
        // 根据异常类型返回不同的提示信息
        String message = "登录已过期，请重新登录";
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未提供token，请先登录";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token无效，请重新登录";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token已过期，请重新登录";
        } else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token已被顶下线，请重新登录";
        } else if (e.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token已被踢下线，请重新登录";
        }
        return Result.error(401, message);
    }
    
    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件上传大小超限", e);
        return Result.error(400, "文件大小超过限制，最大允许10MB");
    }
    
    /**
     * 处理其他系统异常
     * 系统异常需要记录错误日志，但返回给前端的消息要友好
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        // 生产环境不返回详细错误信息，避免泄露系统信息
        String message = "系统错误，请稍后重试";
        // 开发环境可以返回详细错误信息
        // String message = "系统错误: " + e.getMessage();
        return Result.error(500, message);
    }
}

