package com.nala.sharding.service.impl;

import com.nala.sharding.Enum.StrategyEnum;
import com.nala.sharding.service.IStrategyService;
import com.nala.sharding.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
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
@Component(value = "A")
@Slf4j
public class StyAServiceImpl implements IStrategyService {

    @Override
    public String doSomething(String strategy) {
        log.info("该方法策略为：【{}】", strategy);
        return "这是A情况的相关方法";
    }
}
