package com.github.hitzaki.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况，会被全局异常处理器捕获并返回统一的响应格式
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误消息
     */
    private final String message;
    
    /**
     * 构造函数 - 使用默认错误码500
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    /**
     * 构造函数 - 自定义错误码
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    /**
     * 构造函数 - 带原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }
    
    /**
     * 构造函数 - 自定义错误码和原因异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}

