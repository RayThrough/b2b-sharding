package com.nala.sharding.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.nala.sharding.service.IDisruptorService;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义disruptor异常处理类
 *
 */
@Slf4j
public class LongEventExceptionHandler implements ExceptionHandler<LongEvent> {

    private final IDisruptorService disruptorService;

    public LongEventExceptionHandler(IDisruptorService disruptorService) {
        this.disruptorService = disruptorService;
    }

    @Override
    public void handleEventException(Throwable throwable, long sequence, LongEvent longEvent) {
        //事件调用逻辑处理异常
        disruptorService.handle(longEvent.getTableData());
        log.error(">>> Disruptor事件处理异常，进行立即执行补偿操作 >>>");
        log.error("Disruptor 异常信息如下：{}", throwable.getMessage());
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
