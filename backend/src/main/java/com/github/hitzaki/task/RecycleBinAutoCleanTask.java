package com.github.hitzaki.task;

import com.github.hitzaki.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 回收站自动清理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecycleBinAutoCleanTask {
    
    private final RecycleBinService recycleBinService;
    
    /**
     * 每天凌晨2点执行自动清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoClean() {
        log.info("开始执行回收站自动清理任务");
        try {
            recycleBinService.autoClean();
            log.info("回收站自动清理任务执行完成");
        } catch (Exception e) {
            log.error("回收站自动清理任务执行失败", e);
        }
    }
}

