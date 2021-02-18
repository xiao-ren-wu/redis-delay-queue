package org.ywb.redisdelayqueue.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author yuwenbo1
 * @date 2021/1/19 16:09
 * @since 1.0.0
 */
@Slf4j
@Component
public class RedisDelayQueue {

    @Resource
    private DelayQueuePolling delayQueuePolling;

    /**
     * 向延迟队列中投放任务
     *
     * @param keyGen 任务队列名称
     * @param task   任务
     * @param delay  延迟时长，单位毫秒
     */
    public void delay(Supplier<String> keyGen, String topic, Object task, Long delay) {
        delayQueuePolling.delay(keyGen, topic, task, delay);
    }

    /**
     * 向延迟队列中投放任务
     *
     * @param keyGen   任务队列名称
     * @param task     任务
     * @param delay    延迟时长
     * @param timeUnit 延迟时间单位
     */
    public void delay(Supplier<String> keyGen, String topic, Object task, Long delay, TimeUnit timeUnit) {
        delay(keyGen, topic, task, timeUnit.toMillis(delay));
    }

    /**
     * 向延迟队列中投放任务
     *
     * @param task  任务
     * @param delay 延迟时间，单位毫秒
     */
    public void delay(String topic, Object task, Long delay) {
        delay(() -> DateTimeFormatter.ofPattern("yyyyMMddHH").format(LocalDateTime.now()), topic, task, delay);
    }

    /**
     * 向任务队列中投放任务
     *
     * @param task     任务
     * @param delay    延迟时间
     * @param timeUnit 延迟时间单位
     */
    public void delay(String topic, Object task, Long delay, TimeUnit timeUnit) {
        delay(topic, task, timeUnit.toMillis(delay));
    }
}
