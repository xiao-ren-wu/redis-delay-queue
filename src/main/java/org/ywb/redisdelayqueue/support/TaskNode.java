package org.ywb.redisdelayqueue.support;

import lombok.Data;

import java.util.UUID;

/**
 * @author yuwenbo1
 * @date 2021/1/19 16:43
 * @since 1.0.0
 */
@Data
public class TaskNode {
    /**
     * 任务节点
     */
    private String taskJson;
    /**
     * 创建时间戳
     */
    private Long createMillis = System.currentTimeMillis();
    /**
     * 唯一id
     */
    private String id;
    /**
     * 处理名称
     */
    private String topic;

    public static TaskNode of(Object task, String topic) {
        TaskNode taskNode = new TaskNode();
        taskNode.taskJson = GsonHelper.toJson(task);
        taskNode.createMillis = System.currentTimeMillis();
        taskNode.id = UUID.randomUUID().toString();
        taskNode.topic = topic;
        return taskNode;
    }
}
