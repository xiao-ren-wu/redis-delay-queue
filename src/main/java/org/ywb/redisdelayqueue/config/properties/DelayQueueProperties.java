package org.ywb.redisdelayqueue.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yuwenbo1
 * @date 2021/2/18 20:16
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "delay-queue.redis")
public class DelayQueueProperties {

    /**
     * 延迟队列groupList
     */
    private String prefix = "delay-queue";

    /**
     * 消费的群组Id
     */
    private String groupId;

    /**
     * 任务配置
     */
    private TaskProperties task = new TaskProperties(200L);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskProperties {
        /**
         * 间隔多久轮询延迟队列，单位毫秒
         */
        private Long pollingDuration;
    }
}