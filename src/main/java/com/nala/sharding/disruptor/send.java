//package com.nala.sharding.disruptor;
//
//public class send {
//    // 发布事件；
//    RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
//    long sequence = ringBuffer.next();//请求下一个事件序号；
//    try
//
//    {
//        LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
//        long data = getEventData();// 获取要通过事件传递的业务数据；
//        event.set(data);
//    } finally
//
//    {
//        ringBuffer.publish(sequence);//发布事件；
//    }
//
//
//
//
//
//
//
//    static class Translator implements EventTranslatorOneArg<LongEvent, Long> {
//        @Override
//        public void translateTo(LongEvent event, long sequence, Long data) {
//            event.set(data);
//        }
//    }
//
//    public static Translator TRANSLATOR = new Translator();
//
//    public static void publishEvent2(Disruptor<LongEvent> disruptor) {
//        // 发布事件；
//        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
//        long data = getEventData();//获取要通过事件传递的业务数据；
//        ringBuffer.publishEvent(TRANSLATOR, data);
//    }
//}
