package org.ywb.redisdelayqueue.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.ywb.redisdelayqueue.config.properties.DelayQueueProperties;
import org.ywb.redisdelayqueue.support.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author yuwenbo1
 * @date 2021/1/19 17:39
 * @since 1.0.0
 */
@Slf4j
@Component
public class DelayQueuePolling implements ApplicationRunner, ApplicationContextAware {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DelayQueueProperties delayQueueProperties;

    private ApplicationContext applicationContext;

    private Map<String, HandlerTask> taskContainer;

    private String applicationName;

    @Override
    @SuppressWarnings("all")
    public void run(ApplicationArguments args) throws Exception {
        DelayQueueProperties.TaskProperties task = delayQueueProperties.getTask();
        // 这里使用应用名称作为一个namespace隔离
        applicationName = applicationContext.getBean(Environment.class).getProperty("spring.application.name");
        String groupId = delayQueueProperties.getGroupId();
        Assert.hasText(groupId, "延迟队列未设置groupId");
        log.info("\nDelay Queue Get Application Name {}", applicationName);
        Long pollingDuration = task.getPollingDuration();
        fillContainer();
        new Thread(() -> {
            String prefixPattern = String.format("%s:%s:%s:*", delayQueueProperties.getPrefix(), applicationName, groupId);
            while (!Thread.interrupted()) {
                try {
                    polling(prefixPattern);
                    TimeUnit.MILLISECONDS.sleep(pollingDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    log.error(Strings.getStackTraceAsString(e));
                }
            }
        }, "delay queue polling thread").start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void fillContainer() {
        Map<String, HandlerTask> beansOfType = applicationContext.getBeansOfType(HandlerTask.class);
        if (beansOfType.isEmpty()) {
            log.info("未注册消费者到spring");
            return;
        }
        taskContainer = new HashMap<>(beansOfType.size());
        beansOfType.forEach((k, v) -> {
            DelayQueueListener queueListener = v.getClass().getAnnotation(DelayQueueListener.class);
            Assert.notNull(queueListener, "实例[" + k + "]未检测到@DelayQueueListener");
            String topic = queueListener.listen();
            log.info("register delay queue listener [" + topic + "]");
            taskContainer.put(topic, v);
        });
    }

    /**
     * 获取延时队列消息
     *
     * @param pattern zset前缀
     */
    private void polling(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (Objects.isNull(keys) || keys.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("延时队列为空或者不存在...");
            }
            return;
        }
        keys.stream()
                .parallel()
                .forEach(this::pollingCore);
    }

    /**
     * 获取延时队列的头结点，并判读是否过期，如果过期，尝试移除头结点，
     * 移除成功，调用具体的处理任务逻辑
     *
     * @param key zset key
     */
    private void pollingCore(String key) {
        // 获取头结点
        Set<String> values =
                stringRedisTemplate
                        .opsForZSet()
                        .rangeByScore(key, 0, System.currentTimeMillis(), 0, 1);
        if (Objects.isNull(values) || values.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("延时队列[{}]没有过期节点", key);
            }
            return;
        }
        String taskNodeJson = values.iterator().next();
        Boolean remove = Optional.ofNullable(stringRedisTemplate.opsForZSet().remove(key, taskNodeJson))
                .map(a -> a != 0)
                .orElse(false);
        if (remove) {
            log.info("从延时队列移除节点[{}]", taskNodeJson);
            TaskNode taskNode = GsonHelper.toObj(taskNodeJson, TaskNode.class);
            String taskJson = taskNode.getTaskJson();
            String listen = taskNode.getTopic();
            HandlerTask handlerTask = taskContainer.get(listen);
            Assert.notNull(handlerTask, "消息[" + listen + "]未设置监听器");
            handlerTask.handler(taskJson);
        }
    }

    void delay(Supplier<String> keyGen, String topic, Object task, Long delay) {
        TaskNode taskNode = TaskNode.of(task, topic);
        String queueName = String.format("%s:%s:%s:%s", delayQueueProperties.getPrefix(), applicationName, delayQueueProperties.getGroupId(), keyGen.get());
        String taskJson = GsonHelper.toJson(taskNode);
        long score = System.currentTimeMillis() + delay;
        log.info("向延时队列投放任务,队列名称[{}],任务[{}],score[{}]", queueName, taskJson, score);
        stringRedisTemplate.opsForZSet().add(queueName, taskJson, score);
    }

}
