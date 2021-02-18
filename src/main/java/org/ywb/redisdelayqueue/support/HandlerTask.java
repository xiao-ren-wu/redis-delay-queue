package org.ywb.redisdelayqueue.support;

/**
 * @author yuwenbo1
 * @date 2021/1/19 16:40
 * @since 1.0.0
 */
public interface HandlerTask {
    /**
     * 过期节点处理逻辑
     *
     * @param task task
     */
    void handler(String task);
}
