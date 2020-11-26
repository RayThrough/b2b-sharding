package com.nala.sharding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nala.sharding.domain.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
public interface IStrategyService {

    /**
     * 根据策略实现做什么事情
     */
    String doSomething(String strategy);
}
