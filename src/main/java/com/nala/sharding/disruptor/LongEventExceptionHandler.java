package com.nala.sharding.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义disruptor异常处理类
 *
 */
@Slf4j
public class LongEventExceptionHandler implements ExceptionHandler<LongEvent> {



    @Override
    public void handleEventException(Throwable throwable, long l, LongEvent longEvent) {
        //todo 事件调用逻辑处理异常

    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        //启动异常
        log.error("disruptor启动异常,信息是：【{}】", throwable.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        //关闭异常
        log.error("disruptor关闭异常,信息是：【{}】", throwable.getMessage());
    }
}
