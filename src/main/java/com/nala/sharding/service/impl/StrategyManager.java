package com.nala.sharding.service.impl;

import com.nala.sharding.Enum.StrategyEnum;
import com.nala.sharding.service.IStrategyService;
import com.nala.sharding.util.SpringContextUtils;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@Component
public class StrategyManager {

    public String handle(String strategy) {
        IStrategyService bean = (IStrategyService) SpringContextUtils.getBean(strategy);
        return bean.doSomething(strategy);
    }
}
