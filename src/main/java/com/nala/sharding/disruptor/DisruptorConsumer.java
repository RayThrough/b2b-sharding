package com.nala.sharding.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.nala.sharding.service.IDisruptorService;
import com.nala.sharding.service.IStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * disruptor消费者
 */
@Slf4j
public class DisruptorConsumer implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

    /**
     * 创建消费者处理逻辑线程池
     */
    private final Executor executor;

    /**
     * 执行具体逻辑的服务
     */
    private final IDisruptorService disruptorService;

    public DisruptorConsumer(IDisruptorService disruptorService, Executor executor) {
        this.disruptorService = disruptorService;
        this.executor = executor;
    }

    @Override
    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        if (log.isDebugEnabled()) {
            log.info("接受到数据更新请求 >>>{}，Sequence:{}，End Of Batch：{}", longEvent, sequence, endOfBatch);
        }
        this.onEvent(longEvent);
    }

    @Override
    public void onEvent(LongEvent longEvent) throws Exception {
        executor.execute(() -> {
            disruptorService.handle(longEvent.getTableData());
        });
    }
}
