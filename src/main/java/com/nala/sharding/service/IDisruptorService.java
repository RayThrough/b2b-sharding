package com.nala.sharding.service;

import com.nala.sharding.canal.config.TableData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
public interface IDisruptorService {

    /**
     * 根据策略实现做什么事情
     */
    void handle(TableData tableData);
}
