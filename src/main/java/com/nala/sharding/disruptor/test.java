//package com.nala.sharding.disruptor;
//
//public class test {
//    EventFactory<LongEvent> eventFactory = new LongEventFactory();
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；
//    Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory, ringBufferSize,executor,ProducerType.SINGLE,new YieldingWaitStrategy());
//    EventHandler<LongEvent> eventHandler = new LongEventHandler();
//    disruptor.handleEventsWith(eventHandler);
//    disruptor.start();
//}
