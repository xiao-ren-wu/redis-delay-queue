package org.ywb.redisdelayqueue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yuwenbo10
 * @date 2021年1月19日
 */
@Slf4j
@ComponentScan("com.jd.icity.delayqueue")
public class DelayQueueConfiguration {
    public DelayQueueConfiguration() {
        log.info("\ninitialize Redis DelayQueue...");
    }
}
