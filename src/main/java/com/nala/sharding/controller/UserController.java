package com.nala.sharding.controller;


import com.nala.sharding.domain.User;
import com.nala.sharding.mapper.UserMapper;
import com.nala.sharding.service.IStrategyService;
import com.nala.sharding.service.IUserService;
import com.nala.sharding.service.impl.StrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StrategyManager strategyManager;


    @RequestMapping("/strategy")
    public String test(@RequestParam String strategy){
        return strategyManager.handle(strategy);
    }

    @RequestMapping("/test")
    public void test(@RequestParam String name, @RequestParam Integer type){
        User user = new User(name, type);
        userMapper.insert(user);
    }

}
