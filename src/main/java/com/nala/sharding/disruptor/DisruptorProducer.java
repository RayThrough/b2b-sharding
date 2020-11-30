package com.nala.sharding.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.nala.sharding.service.IDisruptorService;
import com.nala.sharding.service.IStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * disruptor生产者，泛指调用 Disruptor 发布事件的用户代码，Disruptor没有定义特定接口或类型。
 */
@Slf4j
@Component
public class DisruptorProducer implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    /**
     * 判断disruptor是否启用
     */
    private volatile boolean isStart;

    /**
     * 创建disruptor
     */
    private final Disruptor<LongEvent> disruptor;

    /**
     * 处理逻辑的具体类
     */
    private final IDisruptorService disruptorService;

    /**
     * 定义disruptor的ringBuffer用于发布和消费事件
     */
    private RingBuffer<LongEvent> ringBuffer;

    /**
     * 创建一个线程池接口类，使用线程池控制disruptor事件发布
     */
    private final ExecutorService executorService;

    /**
     * CPU可使用线程数
     */
    private final int threads;


    /**
     * 构造方法最先执行，初始化参数，类生命周期方法的执行顺序
     * 1.constructor
     * 2.postConstruct注解
     * 3.afterPropertiesSet
     * 4.onApplicationEvent
     */
    @Autowired
    public DisruptorProducer(IDisruptorService disruptorService) {

        //获取系统可用的线程
        threads = Runtime.getRuntime().availableProcessors();
        this.disruptorService = disruptorService;

        //RingBuffer 大小，必须是 2 的 N 次方；
        int ringBufferSize = 1024 * 1024;

        //初始化disruptor，运行需要一个主线程
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, DisruptorThreadFactory.create("Disruptor Main-", false), ProducerType.SINGLE, new YieldingWaitStrategy());

        //初始化用于做事件发布的线程池
        executorService = new ThreadPoolExecutor(threads, threads, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), DisruptorThreadFactory.create("Disruptor Producer-", false), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 该类被初始化或者在容器中被刷新的时候
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info(">>>>>>>> disruptor 启动开始 >>>>>>>>>>>>>");
        //类初始化的时候，启动disruptor
        disruptorStart();
    }

    /**
     * 启动disruptor
     */
    private void disruptorStart() {

        //创建消费者的线程池管理，使用多线程处理消费者逻辑
        final Executor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), DisruptorThreadFactory
                .create("Disruptor consumer-", false), new ThreadPoolExecutor.AbortPolicy());

        DisruptorConsumer[] disruptorConsumers = new DisruptorConsumer[threads];
        for (int i = 0; i < threads; i++) {
            disruptorConsumers[i] = new DisruptorConsumer(disruptorService, executor);
        }

        //定义异常处理
        disruptor.handleEventsWithWorkerPool(disruptorConsumers);
        disruptor.setDefaultExceptionHandler(new LongEventExceptionHandler());
        ringBuffer = disruptor.start();
        this.isStart = true;
    }

    @Override
    public void destroy() throws Exception {
        //如果正在运行，则在类被回收的时候关闭
        if (isStart) {
            disruptor.shutdown();
        }
    }
}
