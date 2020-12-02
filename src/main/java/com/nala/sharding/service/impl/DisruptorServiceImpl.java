package com.nala.sharding.service.impl;

import com.nala.sharding.canal.config.TableData;
import com.nala.sharding.service.IDisruptorService;
import com.nala.sharding.service.IStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@Slf4j
@Service
public class DisruptorServiceImpl implements IDisruptorService {

    @Autowired
    private StrategyManager strategyManager;

    @Override
    public void handle(TableData tableData) {
        if (tableData != null) {
            strategyManager.handle(tableData.getTableName());
        }
    }
}