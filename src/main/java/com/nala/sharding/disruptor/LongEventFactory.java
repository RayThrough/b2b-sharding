package com.nala.sharding.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 定义disruptor事件工厂
 * Disruptor 通过 EventFactory 在 RingBuffer 中预创建 Event 的实例。
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
