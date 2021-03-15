# redis-delay-queue

Redis延时队列

1. 引入依赖

~~~xml
        <dependency>
           <groupId>org.ywb</groupId>
           <artifactId>redis-delay-queue</artifactId>
           <version>0.0.1-SNAPSHOT</version>
        </dependency>
~~~

2. 投递消息

   引入`RedisDelayQueue`

~~~java
    @Resource
    private RedisDelayQueue redisDelayQueue;
    
    void test(){
    	 redisDelayQueue.delay("topicName",task,20L,timeUnit);
    }
~~~

> api 介绍
>
> delay有很多重载方法，我们需要明确下面几个概念
>
> 1. 投递的topic
> 2. 具体投递的任务 Obj
> 3. 希望多久后获得到这个任务
> 4. 时间单位

3. 消费消息

   实现`HandlerTask`接口，并标注消费的`topic`

   handler方法入参是你当时投递到延迟队列中的json串，需要你自己手动反序列化一下

~~~java
@DelayQueueListener(listen = "topic")
public class TaskHandler implements HandlerTask {
    @Override
    public void handler(String task) {
        // todo
    }
}
~~~

### since 1.0.4
#### 新增
1. 期望获取时间参数，可以通过直接设置延迟任务获取时间
2. 增加`Supplier`,`Function`函数式接口用于制定延迟任务



