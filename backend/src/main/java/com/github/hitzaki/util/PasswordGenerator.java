package com.github.hitzaki.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成BCrypt加密后的密码，方便直接更新数据库
 *
 * 使用方法：
 * 1. 直接运行main方法，修改代码中的 plainPassword 变量
 * 2. 或者通过命令行参数传入密码：java PasswordGenerator your_password
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String plainPassword;

        // 如果通过命令行参数传入密码，使用命令行参数；否则使用代码中的默认值
        if (args.length > 0) {
            plainPassword = args[0];
        } else {
            // 在这里修改你要加密的密码（如果没有通过命令行参数传入）
            plainPassword = "a1134443466";
        }

        // 生成加密后的密码
        String encodedPassword = encoder.encode(plainPassword);

        System.out.println("========================================");
        System.out.println("原始密码: " + plainPassword);
        System.out.println("加密后密码: " + encodedPassword);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL更新语句示例:");
        System.out.println("UPDATE `user` SET `password` = '" + encodedPassword + "' WHERE `username` = 'your_username';");
        System.out.println("========================================");
        System.out.println();
        System.out.println("提示：");
        System.out.println("1. 直接运行：修改代码中的 plainPassword 变量后运行");
        System.out.println("2. 命令行运行：java PasswordGenerator your_password");
    }
}

