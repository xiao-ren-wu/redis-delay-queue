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
     * @param task  任务
     * @param delay 延迟时间，单位毫秒
     */
    private void delay(String topic, Object task, Long delay) {
        delayQueuePolling.delay(() -> DateTimeFormatter.ofPattern("yyyyMMddHH").format(LocalDateTime.now()), topic, task, delay);
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

    /**
     * 向延迟队列中投放任务
     *
     * @param topic    队列的topic
     * @param supplier 延迟任务制造函数
     * @param delay    延迟时间
     * @param timeUnit 延迟时间单位
     * @since 1.0.1
     */
    public void delay(String topic, Supplier<Object> supplier, Long delay, TimeUnit timeUnit) {
        delay(topic, supplier.get(), delay, timeUnit);
    }

    /**
     * 向延迟队列中投放任务
     *
     * @param topic    队列的topic
     * @param supplier 延迟任务制造函数
     * @param endTime  期望获取到延迟任务的时间
     * @since 1.0.1
     */
    public void delay(String topic, Supplier<Object> supplier, LocalDateTime endTime) {
        delay(topic, supplier.get(), getDelayTime(endTime), TimeUnit.MILLISECONDS);
    }

    /**
     * 向延迟队列中投放任务
     *
     * @param topic    队列的topic
     * @param function 延迟任务制造函数
     * @param t        延迟任务制造函数入参
     * @param delay    延迟时间
     * @param timeUnit 延迟时间单位
     * @since 1.0.1
     */
    public <T> void delay(String topic, Function<T, Object> function, T t, Long delay, TimeUnit timeUnit) {
        delay(topic, function.apply(t), delay, timeUnit);
    }

    /**
     * 向延迟队列中投放任务
     *
     * @param topic    队列的topic
     * @param function 延迟任务制造函数
     * @param t        延迟任务制造函数入参
     * @param endTime  期望获取到延迟任务的时间
     * @since 1.0.1
     */
    public <T> void delay(String topic, Function<T, Object> function, T t, LocalDateTime endTime) {
        delay(topic, function.apply(t), getDelayTime(endTime), TimeUnit.MILLISECONDS);
    }

    private long getDelayTime(LocalDateTime endTime) {
        final long endTimeMilli = endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        final long currentMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return currentMilli - endTimeMilli;
    }
}
