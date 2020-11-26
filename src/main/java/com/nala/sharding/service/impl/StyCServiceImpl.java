package com.nala.sharding.service.impl;

import com.nala.sharding.service.IStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@Component(value = "C")
@Slf4j
public class StyCServiceImpl implements IStrategyService {

    @Override
    public String doSomething(String strategy) {
        log.info("该方法策略为：【{}】", strategy);
        return "这是C情况的相关方法";
    }
}
