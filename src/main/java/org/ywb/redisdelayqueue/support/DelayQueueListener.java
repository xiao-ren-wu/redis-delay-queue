package org.ywb.redisdelayqueue.support;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author yuwenbo1
 * @date 2021/1/19 18:02
 * @since 1.0.0
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DelayQueueListener {
    /**
     * 监听的topic信息，支持string以及${prop}形式
     *
     * @return topic
     */
    String listen();
}
